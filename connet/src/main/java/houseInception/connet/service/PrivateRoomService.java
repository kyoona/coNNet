package houseInception.connet.service;

import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.User;
import houseInception.connet.domain.privateRoom.PrivateChat;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;
import houseInception.connet.dto.*;
import houseInception.connet.exception.ChatEmojiException;
import houseInception.connet.exception.PrivateRoomException;
import houseInception.connet.exception.UserException;
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

    private final SocketServiceProvider socketServiceProvider;
    private final S3ServiceProvider s3ServiceProvider;
    private final PrivateRoomRepository privateRoomRepository;
    private final UserBlockRepository userBlockRepository;
    private final UserRepository userRepository;
    private final EntityManager em;

    @Value("${aws.s3.imageUrlPrefix}")
    private String s3UrlPrefix;

    @Transactional
    public PrivateChatAddRestDto addPrivateChat(Long userId, Long targetId, PrivateChatAddDto chatAddDto) {
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
        if (privateRoomReceiver.getStatus() == DELETED) {
            privateRoom.setPrivateRoomUserAlive(privateRoomReceiver, privateChat.getCreatedAt());
        }

        PrivateChatSocketDto privateChatSocketDto =
                new PrivateChatSocketDto(privateRoom.getPrivateRoomUuid(), chatAddDto.getMessage(), imgUrl, ChatterRole.USER, user, privateChat.getCreatedAt());
        socketServiceProvider.sendMessage(targetId, privateChatSocketDto);

        return new PrivateChatAddRestDto(privateRoom.getPrivateRoomUuid());
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

        List<PrivateChatResDto> privateChatList = privateRoomRepository.getPrivateChatList(privateRoomId, page);

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
            throw new ChatEmojiException(NOT_CHATROOM_USER);
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
