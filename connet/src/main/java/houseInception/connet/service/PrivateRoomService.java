package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;
import houseInception.connet.dto.PrivateChatAddDto;
import houseInception.connet.dto.PrivateChatAddRestDto;
import houseInception.connet.exception.PrivateRoomException;
import houseInception.connet.exception.UserException;
import houseInception.connet.repository.PrivateRoomRepository;
import houseInception.connet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.response.status.BaseErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PrivateRoomService {

    private final PrivateRoomRepository privateRoomRepository;
    private final UserRepository userRepository;

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

        PrivateRoomUser privateRoomUser = findPrivateRoomUser(privateRoom.getId(), userId);
        if (chatAddDto.getMessage() != null) {
            privateRoom.addUserToUserChat(chatAddDto.getMessage(), privateRoomUser);
        }

        //파일일 경우 S3에 저장후 url 공유 로직

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
