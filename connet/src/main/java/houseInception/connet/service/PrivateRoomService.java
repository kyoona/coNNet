package houseInception.connet.service;

import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.User;
import houseInception.connet.domain.privateRoom.PrivateChat;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;
import houseInception.connet.dto.*;
import houseInception.connet.exception.PrivateRoomException;
import houseInception.connet.exception.UserException;
import houseInception.connet.externalServiceProvider.gpt.GptApiProvider;
import houseInception.connet.externalServiceProvider.s3.S3ServiceProvider;
import houseInception.connet.repository.PrivateRoomRepository;
import houseInception.connet.repository.UserBlockRepository;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.socketManager.SocketServiceProvider;
import houseInception.connet.socketManager.dto.PrivateChatSocketDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.Status.DELETED;
import static houseInception.connet.response.status.BaseErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PrivateRoomService {

    private final GptApiProvider gptApiProvider;
    private final SocketServiceProvider socketServiceProvider;
    private final S3ServiceProvider s3ServiceProvider;
    private final PrivateRoomRepository privateRoomRepository;
    private final UserBlockRepository userBlockRepository;
    private final UserRepository userRepository;
    private final EntityManager em;

    @Value("${aws.s3.imageUrlPrefix}")
    private String s3UrlPrefix;

    @Transactional
    public PrivateChatAddResDto addPrivateChat(Long userId, Long targetId, PrivateChatAddDto chatAddDto) {
        User targetUser = findUser(targetId);
        User user = findUser(userId);

        checkHasUserBlock(userId, targetId);
        checkValidContent(chatAddDto.getMessage(), chatAddDto.getImage());

        PrivateRoom privateRoom;
        if (chatAddDto.getChatRoomUuid() == null || chatAddDto.getChatRoomUuid().isBlank()) {
            privateRoom = PrivateRoom.create(user, targetUser);
            privateRoomRepository.save(privateRoom);
        } else {
            privateRoom = findPrivateRoom(chatAddDto.getChatRoomUuid());
        }

        PrivateRoomUser privateRoomSender = findPrivateRoomUser(privateRoom.getId(), userId);

        String imgUrl = uploadImages(chatAddDto.getImage());

        PrivateChat privateChat = privateRoom.addUserToUserChat(chatAddDto.getMessage(), imgUrl, privateRoomSender);
        em.flush();

        PrivateRoomUser privateRoomReceiver = findPrivateRoomUser(privateRoom.getId(), targetId);
        checkRoomUserAndSetAlive(privateRoomReceiver, privateRoom, privateChat.getCreatedAt());
        checkRoomUserAndSetAlive(privateRoomSender, privateRoom, privateChat.getCreatedAt());

        sendMessageThrowSocket(targetId, privateRoom.getPrivateRoomUuid(), privateChat.getId(), chatAddDto.getMessage(), imgUrl, ChatterRole.USER, user, privateChat.getCreatedAt());

        return new PrivateChatAddResDto(privateRoom.getPrivateRoomUuid(), privateChat.getId());
    }

    private void checkRoomUserAndSetAlive(PrivateRoomUser privateRoomUser, PrivateRoom privateRoom, LocalDateTime participationTime){
        if (privateRoomUser.getStatus() == DELETED) {
            privateRoom.setPrivateRoomUserAlive(privateRoomUser, participationTime);
        }
    }

    @Transactional
    public Long deletePrivateRoom(Long userId, String privateRoomUuid) {
        PrivateRoom privateRoom = findPrivateRoom(privateRoomUuid);
        PrivateRoomUser privateRoomUser = findPrivateRoomUser(privateRoom.getId(), userId);

        privateRoom.setPrivateRoomUserDelete(privateRoomUser);
        return privateRoomUser.getId();
    }

    private String uploadImages(MultipartFile image){
        if (image == null) {
            return null;
        }

        String newFileName = getUniqueFileName(image.getOriginalFilename());
        s3ServiceProvider.uploadImage(newFileName, image);

        return s3UrlPrefix + newFileName;
    }

    private String getUniqueFileName(String originalFileName){
        int extensionIndex = originalFileName.lastIndexOf(".");
        if(extensionIndex == -1){
            throw new PrivateRoomException(NO_VALID_FILE_NAME);
        }

        String extension = originalFileName.substring(extensionIndex);

        return UUID.randomUUID() + extension;
    }

    private void checkValidContent(String message, MultipartFile images) {
        boolean hasMessage = StringUtils.hasText(message);
        boolean hasImages = images != null;

        if (!hasMessage && !hasImages) {
            throw new PrivateRoomException(NO_CONTENT_IN_CHAT);
        }
    }

    @Transactional
    public GptPrivateChatAddResDto addGptChat(Long userId, String privateRoomUuid, String message) {
        PrivateRoom privateRoom = findPrivateRoom(privateRoomUuid);
        PrivateRoomUser privateRoomSender = findPrivateRoomUser(privateRoom.getId(), userId);
        User user = findUser(userId);

        PrivateChat privateChat = privateRoom.addUserToGptChat(message, privateRoomSender);
        em.flush();

        PrivateRoomUser privateRoomReceiver = privateRoomRepository.findTargetRoomUserWithUserInChatRoom(userId, privateRoom.getId())
                .orElseThrow(() -> new PrivateRoomException(INTERNAL_SERVER_ERROR, "개인 채팅방에 상대 유저가 존재하지 않습니다."));
        Long targetId = privateRoomReceiver.getUser().getId();
        sendMessageThrowSocket(targetId, privateRoomUuid, privateChat.getId(), message, null, ChatterRole.USER, user, privateChat.getCreatedAt());

        String gptResponse = gptApiProvider.getChatCompletion(message);
        PrivateChat gptPrivateChat = privateRoom.addGptToUserChat(gptResponse);
        em.flush();

        sendMessageThrowSocket(targetId, privateRoomUuid, gptPrivateChat.getId(), message, null, ChatterRole.USER, user, gptPrivateChat.getCreatedAt());

        checkRoomUserAndSetAlive(privateRoomReceiver, privateRoom, privateChat.getCreatedAt());
        checkRoomUserAndSetAlive(privateRoomSender, privateRoom, privateChat.getCreatedAt());

        return new GptPrivateChatAddResDto(privateChat.getId(), privateChat.getCreatedAt(), gptPrivateChat.getId(), gptPrivateChat.getCreatedAt(), gptResponse);
    }

    private void sendMessageThrowSocket(Long targetId, String roomUuid, Long chatId, String message, String image, ChatterRole chatterRole, User sender, LocalDateTime createdAt){
        PrivateChatSocketDto chatSocketDto =
                new PrivateChatSocketDto(roomUuid, chatId, message, image, chatterRole, sender, createdAt);
        socketServiceProvider.sendMessage(targetId, chatSocketDto);
    }

    public DataListResDto<PrivateRoomResDto> getPrivateRoomList(Long userId, int page) {
        Map<Long, PrivateRoomResDto> privateRoomMap = privateRoomRepository.getPrivateRoomList(userId, page);

        List<Long> privateRoomIdList = privateRoomMap.values()
                .stream()
                .map(PrivateRoomResDto::getChatRoomId)
                .toList();
        List<Long> privateRoomIdOfActiveTimeOrder = privateRoomRepository.getLastChatTimeOfPrivateRooms(privateRoomIdList);

        List<PrivateRoomResDto> resultList = privateRoomIdOfActiveTimeOrder.stream()
                .map(privateRoomId -> privateRoomMap.get(privateRoomId))
                .toList();

        return new DataListResDto<>(page, resultList);
    }

    public DataListResDto<PrivateChatResDto> getPrivateChatList(Long userId, String privateRoomUuid, int page) {
        Long privateRoomId = checkExistPrivateRoomAndGetId(privateRoomUuid);
        checkUserInPrivateRoom(userId, privateRoomUuid);

        List<PrivateChatResDto> privateChatList = privateRoomRepository.getPrivateChatList(userId, privateRoomId, page);

        return new DataListResDto<>(page, privateChatList);
    }

    private Long checkExistPrivateRoomAndGetId(String privateRoomUuid){
        Long privateRoomId = privateRoomRepository.findIdByPrivateRoomUuid(privateRoomUuid);
        if (privateRoomId == null){
            throw new PrivateRoomException(NO_SUCH_CHATROOM);
        }

        return privateRoomId;
    }

    private void checkUserInPrivateRoom(Long userId, String privateRoomUuid) {
        if (!privateRoomRepository.existsAlivePrivateRoomUser(userId, privateRoomUuid)){
            throw new PrivateRoomException(NOT_CHATROOM_USER);
        }
    }

    private void checkHasUserBlock(Long userId, Long targetId) {
        if(userBlockRepository.existsByUserIdAndTargetId(userId, targetId)){
            throw new PrivateRoomException(BLOCK_USER);
        }
    }

    private User findUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NO_SUCH_USER));
    }

    private PrivateRoom findPrivateRoom(String privateRoomUuid){
        return privateRoomRepository.findByPrivateRoomUuidAndStatus(privateRoomUuid, ALIVE)
                .orElseThrow(() -> new PrivateRoomException(NO_SUCH_CHATROOM));
    }

    private PrivateRoomUser findPrivateRoomUser(Long privateRoomId, Long userId){
        return privateRoomRepository.findPrivateRoomUser(privateRoomId, userId)
                .orElseThrow(() -> new PrivateRoomException(NOT_CHATROOM_USER));
    }
}
