package houseInception.connet.service;

import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.user.User;
import houseInception.connet.domain.privateRoom.PrivateChat;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;
import houseInception.connet.dto.*;
import houseInception.connet.dto.privateRoom.*;
import houseInception.connet.exception.PrivateRoomException;
import houseInception.connet.exception.SocketException;
import houseInception.connet.externalServiceProvider.gpt.GptApiProvider;
import houseInception.connet.externalServiceProvider.s3.S3ServiceProvider;
import houseInception.connet.repository.ChatReadLogRepository;
import houseInception.connet.repository.PrivateRoomRepository;
import houseInception.connet.response.status.BaseErrorCode;
import houseInception.connet.service.util.CommonDomainService;
import houseInception.connet.socketManager.SocketServiceProvider;
import houseInception.connet.socketManager.dto.PrivateChatSocketDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static houseInception.connet.domain.ChatterRole.GPT;
import static houseInception.connet.domain.ChatterRole.USER;
import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.Status.DELETED;
import static houseInception.connet.response.status.BaseErrorCode.*;
import static houseInception.connet.service.util.FileUtil.getUniqueFileName;
import static houseInception.connet.service.util.FileUtil.isInValidFile;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PrivateRoomService {

    private final PrivateRoomRepository privateRoomRepository;
    private final ChatReadLogRepository chatReadLogRepository;
    private final EntityManager em;
    private final CommonDomainService domainService;
    private final GptApiProvider gptApiProvider;
    private final SocketServiceProvider socketServiceProvider;
    private final S3ServiceProvider s3ServiceProvider;

    @Transactional
    public PrivateChatAddResDto addPrivateChat(Long userId, Long targetId, PrivateChatAddDto chatAddDto) {
        checkConnectedUserSocket(userId);

        User targetUser = domainService.findUser(targetId);
        User user = domainService.findUser(userId);

        checkValidContent(chatAddDto.getMessage(), chatAddDto.getImage());
        domainService.checkNotUserBlock(userId, targetId);

        Optional<PrivateRoom> nullablePrivateRoom = privateRoomRepository.findPrivateRoomByUsers(userId, targetId);
        PrivateRoom privateRoom = getOrCreatePrivateRoom(nullablePrivateRoom, user, targetUser);

        String imgUrl = uploadImages(chatAddDto.getImage());

        PrivateRoomUser privateRoomSender = findPrivateRoomUser(privateRoom.getId(), userId);
        PrivateChat privateChat = privateRoom.addUserToUserChat(chatAddDto.getMessage(), imgUrl, privateRoomSender);
        em.flush();

        PrivateRoomUser privateRoomReceiver = findPrivateRoomUser(privateRoom.getId(), targetId);
        checkRoomUserDeletedAndSetAlive(privateRoomReceiver, privateRoom, privateChat.getCreatedAt());
        checkRoomUserDeletedAndSetAlive(privateRoomSender, privateRoom, privateChat.getCreatedAt());

        sendMessageThrowSocket(targetId, privateRoom.getPrivateRoomUuid(), privateChat, USER, user);

        return new PrivateChatAddResDto(privateRoom.getPrivateRoomUuid(), privateChat.getId(), privateChat.getCreatedAt());
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
        if (isInValidFile(image)) {
            return null;
        }

        String newFileName = getUniqueFileName(image.getOriginalFilename());
        return s3ServiceProvider.uploadImage(newFileName, image);
    }


    private void checkValidContent(String message, MultipartFile image) {
        if(isInValidFile(image) && !StringUtils.hasText(message)){
            throw new PrivateRoomException(NO_CONTENT_IN_CHAT);
        }
    }

    @Transactional
    public GptPrivateChatAddResDto addGptChat(Long userId, Long targetId, String message) {
        checkConnectedUserSocket(userId);

        User targetUser = domainService.findUser(targetId);
        User user = domainService.findUser(userId);

        domainService.checkNotUserBlock(userId, targetId);

        Optional<PrivateRoom> nullablePrivateRoom = privateRoomRepository.findPrivateRoomByUsers(userId, targetId);
        PrivateRoom privateRoom = getOrCreatePrivateRoom(nullablePrivateRoom, user, targetUser);

        PrivateRoomUser privateRoomSender = findPrivateRoomUser(privateRoom.getId(), userId);
        PrivateChat privateChat = privateRoom.addUserToGptChat(message, privateRoomSender);
        em.flush();

        PrivateRoomUser privateRoomReceiver = privateRoomRepository.findPrivateRoomUser(privateRoom.getId(), targetId)
                .orElseThrow(() -> new PrivateRoomException(INTERNAL_SERVER_ERROR, "개인 채팅방에 상대 유저가 존재하지 않습니다."));
        sendMessageThrowSocket(targetId, privateRoom.getPrivateRoomUuid(), privateChat, USER, user);

        checkRoomUserDeletedAndSetAlive(privateRoomReceiver, privateRoom, privateChat.getCreatedAt());
        checkRoomUserDeletedAndSetAlive(privateRoomSender, privateRoom, privateChat.getCreatedAt());


        Optional<String> lastGptChat = privateRoomRepository.findLastGptChat(privateRoom.getId());
        String gptResponse = lastGptChat.isEmpty()
                ? gptApiProvider.getChatCompletion(message)
                : gptApiProvider.getChatCompletionWithPrevContent(message, lastGptChat.get());
        PrivateChat gptPrivateChat = privateRoom.addGptToUserChat(gptResponse);
        em.flush();

        sendMessageThrowSocket(targetId, privateRoom.getPrivateRoomUuid(), gptPrivateChat, GPT, null);

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

    private void sendMessageThrowSocket(Long targetId, String roomUuid, PrivateChat chat, ChatterRole chatterRole, User sender) {
        PrivateChatSocketDto chatSocketDto =
                new PrivateChatSocketDto(roomUuid, chat, chatterRole, sender);
        socketServiceProvider.sendMessage(targetId, chatSocketDto);
    }

    public DataListResDto<PrivateRoomResDto> getPrivateRoomList(Long userId, int page) {
        Map<Long, PrivateRoomResDto> privateRoomMap = privateRoomRepository.getPrivateRoomList(userId, page);

        List<Long> privateRoomIdList = privateRoomMap.values()
                .stream()
                .map(PrivateRoomResDto::getChatRoomId)
                .toList();
        List<Long> privateRoomIdOfActiveTimeOrder = privateRoomRepository.findLastChatTimeOfPrivateRooms(privateRoomIdList);

        List<String> privateRoomUuidList = privateRoomMap.values()
                .stream()
                .map(PrivateRoomResDto::getChatRoomUuid)
                .toList();
        Map<Long, Long> recentReadLogs = chatReadLogRepository.findRecentReadLogOfPrivateRooms(privateRoomUuidList);
        Map<Long, Long> recentChats = privateRoomRepository.findRecentChatOfRooms(privateRoomIdList);

        List<PrivateRoomResDto> resultList = privateRoomIdOfActiveTimeOrder.stream()
                .map(privateRoomId -> {
                    PrivateRoomResDto privateRoomResDto = privateRoomMap.get(privateRoomId);
                    privateRoomResDto.setUnread(recentChats, recentReadLogs);

                    return privateRoomResDto;
                })
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

    private PrivateRoom findPrivateRoom(String privateRoomUuid){
        return privateRoomRepository.findByPrivateRoomUuidAndStatus(privateRoomUuid, ALIVE)
                .orElseThrow(() -> new PrivateRoomException(NO_SUCH_CHATROOM));
    }

    private PrivateRoomUser findPrivateRoomUser(Long privateRoomId, Long userId){
        return privateRoomRepository.findPrivateRoomUser(privateRoomId, userId)
                .orElseThrow(() -> new PrivateRoomException(NOT_CHATROOM_USER));
    }

    private void checkConnectedUserSocket(Long userId){
        if(!socketServiceProvider.isUserConnectedSocket(userId)){
            throw new SocketException(UNCONNECTED_SOCKET);
        }
    }
}
