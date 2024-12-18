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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.Status.DELETED;
import static houseInception.connet.response.status.BaseErrorCode.*;

@Slf4j
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

        checkValidContent(chatAddDto.getMessage(), chatAddDto.getImage());
        checkHasUserBlock(userId, targetId);

        Optional<PrivateRoom> nullablePrivateRoom = privateRoomRepository.findPrivateRoomByUsers(userId, targetId);
        PrivateRoom privateRoom = getOrCreatePrivateRoom(nullablePrivateRoom, user, targetUser);

        String imgUrl = uploadImages(chatAddDto.getImage());

        PrivateRoomUser privateRoomSender = findPrivateRoomUser(privateRoom.getId(), userId);
        PrivateChat privateChat = privateRoom.addUserToUserChat(chatAddDto.getMessage(), imgUrl, privateRoomSender);
        em.flush();

        PrivateRoomUser privateRoomReceiver = findPrivateRoomUser(privateRoom.getId(), targetId);
        checkRoomUserDeletedAndSetAlive(privateRoomReceiver, privateRoom, privateChat.getCreatedAt());
        checkRoomUserDeletedAndSetAlive(privateRoomSender, privateRoom, privateChat.getCreatedAt());

        sendMessageThrowSocket(targetId, privateRoom.getPrivateRoomUuid(), privateChat.getId(), chatAddDto.getMessage(), imgUrl, ChatterRole.USER, user, privateChat.getCreatedAt());

        return new PrivateChatAddResDto(privateRoom.getPrivateRoomUuid(), privateChat.getId());
    }

    private void checkRoomUserDeletedAndSetAlive(PrivateRoomUser privateRoomUser, PrivateRoom privateRoom, LocalDateTime participationTime){
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
    public GptPrivateChatAddResDto addGptChat(Long userId, Long targetId, String message) {
        User targetUser = findUser(targetId);
        User user = findUser(userId);

        checkHasUserBlock(userId, targetId);

        Optional<PrivateRoom> nullablePrivateRoom = privateRoomRepository.findPrivateRoomByUsers(userId, targetId);
        PrivateRoom privateRoom = getOrCreatePrivateRoom(nullablePrivateRoom, user, targetUser);

        PrivateRoomUser privateRoomSender = findPrivateRoomUser(privateRoom.getId(), userId);
        PrivateChat privateChat = privateRoom.addUserToGptChat(message, privateRoomSender);
        em.flush();

        PrivateRoomUser privateRoomReceiver = privateRoomRepository.findPrivateRoomUser(privateRoom.getId(), targetId)
                .orElseThrow(() -> new PrivateRoomException(INTERNAL_SERVER_ERROR, "개인 채팅방에 상대 유저가 존재하지 않습니다."));
        sendMessageThrowSocket(targetId, privateRoom.getPrivateRoomUuid(), privateChat.getId(), message, null, ChatterRole.USER, user, privateChat.getCreatedAt());

        checkRoomUserDeletedAndSetAlive(privateRoomReceiver, privateRoom, privateChat.getCreatedAt());
        checkRoomUserDeletedAndSetAlive(privateRoomSender, privateRoom, privateChat.getCreatedAt());

        String gptResponse = gptApiProvider.getChatCompletion(message);
        PrivateChat gptPrivateChat = privateRoom.addGptToUserChat(gptResponse);
        em.flush();

        sendMessageThrowSocket(targetId, privateRoom.getPrivateRoomUuid(), gptPrivateChat.getId(), gptResponse, null, ChatterRole.GPT, user, gptPrivateChat.getCreatedAt());

        return new GptPrivateChatAddResDto(privateRoom.getPrivateRoomUuid(), privateChat.getId(), privateChat.getCreatedAt(), gptPrivateChat.getId(), gptPrivateChat.getCreatedAt(), gptResponse);
    }
    
    private PrivateRoom getOrCreatePrivateRoom(Optional<PrivateRoom> nullablePrivateRoom, User user, User targetUser){
        PrivateRoom privateRoom;
        
        if (nullablePrivateRoom.isEmpty()) {
            privateRoom = PrivateRoom.create(user, targetUser);
            privateRoomRepository.save(privateRoom);
        } else {
            privateRoom = nullablePrivateRoom.get();
        }
        
        return privateRoom;
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

    public DataListResDto<PrivateChatResDto> getPrivateChatList(Long userId, Long targetId, int page) {
        Optional<PrivateRoom> privateRoom = privateRoomRepository.findPrivateRoomByUsers(userId, targetId);
        if(privateRoom.isEmpty()){
            return new DataListResDto<>(page, new ArrayList<>());
        }

        List<PrivateChatResDto> privateChatList = privateRoomRepository.getPrivateChatList(userId, privateRoom.get().getId(), page);

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
