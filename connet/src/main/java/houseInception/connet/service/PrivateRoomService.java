package houseInception.connet.service;

import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.User;
import houseInception.connet.domain.privateRoom.PrivateChat;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;
import houseInception.connet.dto.PrivateChatAddDto;
import houseInception.connet.dto.PrivateChatAddRestDto;
import houseInception.connet.exception.PrivateRoomException;
import houseInception.connet.exception.UserException;
import houseInception.connet.repository.PrivateRoomRepository;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.socketManager.SocketServiceProvider;
import houseInception.connet.socketManager.dto.PrivateChatResDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.Status.DELETED;
import static houseInception.connet.response.status.BaseErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PrivateRoomService {

    private final SocketServiceProvider socketServiceProvider;
    private final PrivateRoomRepository privateRoomRepository;
    private final UserRepository userRepository;
    private final EntityManager em;

    @Transactional
    public PrivateChatAddRestDto addPrivateChat(Long userId, Long targetId, PrivateChatAddDto chatAddDto) {
        User targetUser = findUser(targetId);
        User user = findUser(userId);

        checkValidContent(chatAddDto.getMessage(), chatAddDto.getImages());

        PrivateRoom privateRoom;
        if (chatAddDto.getChatRoomUuid() == null) {
            privateRoom = PrivateRoom.create(user, targetUser);
            privateRoomRepository.save(privateRoom);
        } else {
            privateRoom = findPrivateRoom(chatAddDto.getChatRoomUuid());
        }

        PrivateRoomUser privateRoomSender = findPrivateRoomUser(privateRoom.getId(), userId);

        //파일일 경우 S3에 저장후 url 공유 로직
        PrivateChat privateChat = privateRoom.addUserToUserChat(chatAddDto.getMessage(), privateRoomSender);
        em.flush();

        PrivateRoomUser privateRoomReceiver = findPrivateRoomUser(privateRoom.getId(), targetId);
        if (privateRoomReceiver.getStatus() == DELETED) {
            privateRoom.setPrivateRoomUserAlive(privateRoomReceiver, privateChat.getCreatedAt());
        }

        PrivateChatResDto privateChatResDto =
                new PrivateChatResDto(privateRoom.getPrivateRoomUuid(), chatAddDto.getMessage(), null, ChatterRole.USER, user, privateChat.getCreatedAt());
        socketServiceProvider.sendMessage(targetId, privateChatResDto);

        return new PrivateChatAddRestDto(privateRoom.getPrivateRoomUuid());
    }

    private void checkValidContent(String message, List<MultipartFile> images) {
        boolean hasMessage = StringUtils.hasText(message);
        boolean hasImages = images != null && !images.isEmpty();

        if (!hasMessage && !hasImages) {
            throw new PrivateRoomException(NO_CONTENT_IN_CHAT);
        }
    }


    private void checkExistUser(Long userId){
        if (!userRepository.existsByIdAndStatus(userId, ALIVE)){
            throw new UserException(NO_SUCH_USER);
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
