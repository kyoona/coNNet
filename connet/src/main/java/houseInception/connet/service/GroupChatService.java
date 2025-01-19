package houseInception.connet.service;

import houseInception.connet.domain.GroupChat;
import houseInception.connet.domain.user.User;
import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.dto.DataListResDto;
import houseInception.connet.dto.groupChat.*;
import houseInception.connet.exception.ChannelException;
import houseInception.connet.exception.GroupChatException;
import houseInception.connet.exception.GroupException;
import houseInception.connet.externalServiceProvider.gpt.GptApiProvider;
import houseInception.connet.externalServiceProvider.s3.S3ServiceProvider;
import houseInception.connet.repository.ChannelRepository;
import houseInception.connet.repository.GroupChatRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.service.util.CommonDomainService;
import houseInception.connet.socketManager.SocketServiceProvider;
import houseInception.connet.socketManager.dto.GroupChatSocketDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static houseInception.connet.domain.ChatterRole.GPT;
import static houseInception.connet.domain.ChatterRole.USER;
import static houseInception.connet.response.status.BaseErrorCode.*;
import static houseInception.connet.service.util.FileUtil.getUniqueFileName;
import static houseInception.connet.service.util.FileUtil.isInValidFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;
    private final ChannelRepository channelRepository;
    private final GroupRepository groupRepository;
    private final CommonDomainService domainService;
    private final S3ServiceProvider s3ServiceProvider;
    private final SocketServiceProvider socketServiceProvider;
    private final GptApiProvider gptApiProvider;

    @Transactional
    public GroupChatAddResDto addChat(Long userId, String groupUuid, GroupChatAddDto chatAddDto) {
        Long groupId = findGroupIdByUuid(groupUuid);
        checkExistTapInGroup(chatAddDto.getTapId(), groupUuid);
        checkValidContent(chatAddDto.getImage(), chatAddDto.getMessage());
        GroupUser groupUser = findGroupUser(userId, groupUuid);
        User user = domainService.findUser(userId);

        String imageUrl = uploadImages(chatAddDto.getImage());

        GroupChat chat = GroupChat.createUserToUser(groupId, groupUser, chatAddDto.getTapId(), chatAddDto.getMessage(), imageUrl);
        groupChatRepository.save(chat);

        GroupChatSocketDto socketDto = new GroupChatSocketDto(groupUuid, chat, USER, user);
        sendMessageToGroupUsers(groupUuid, userId, socketDto);

        return new GroupChatAddResDto(chat.getId(), chat.getCreatedAt());
    }

    private void sendMessageToGroupUsers(String groupUuid, Long userId, GroupChatSocketDto socketDto){
        List<Long> groupUserIds = groupRepository.findUserIdsOfGroupExceptUser(groupUuid, userId);
        groupUserIds.forEach((targetId) -> socketServiceProvider.sendMessage(targetId, socketDto));
    }

    private String uploadImages(MultipartFile image){
        if (isInValidFile(image)) {
            return null;
        }

        String newFileName = getUniqueFileName(image.getOriginalFilename());
        return s3ServiceProvider.uploadImage(newFileName, image);
    }

    @Transactional
    public GroupGptChatAddResDto addGptChat(Long userId, String groupUuid, GroupGptChatAddDto chatAddDto) {
        Long groupId = findGroupIdByUuid(groupUuid);
        checkExistTapInGroup(chatAddDto.getTapId(), groupUuid);
        GroupUser groupUser = findGroupUser(userId, groupUuid);
        User user = domainService.findUser(userId);

        GroupChat userChat = GroupChat.createUserToGpt(groupId, groupUser, chatAddDto.getTapId(), chatAddDto.getMessage());
        groupChatRepository.save(userChat);

        List<Long> groupUserIds = groupRepository.findUserIdsOfGroupExceptUser(groupUuid, userId);
        GroupChatSocketDto userSocketDto = new GroupChatSocketDto(groupUuid, userChat, USER, user);
        sendMessageToGroupUsers(groupUserIds, userSocketDto);

        Optional<String> lastGptChat = groupChatRepository.findLastGptChatOfTap(chatAddDto.getTapId());
        String gptMessage = lastGptChat.isEmpty()
                ? gptApiProvider.getChatCompletion(userChat.getMessage())
                : gptApiProvider.getChatCompletionWithPrevContent(userChat.getMessage(), lastGptChat.get());
        GroupChat gptChat = GroupChat.createGptToUser(groupId, chatAddDto.getTapId(), gptMessage);
        groupChatRepository.save(gptChat);

        GroupChatSocketDto gptSocketDto = new GroupChatSocketDto(groupUuid, gptChat, GPT, null);
        sendMessageToGroupUsers(groupUserIds, gptSocketDto);

        return new GroupGptChatAddResDto(userChat.getId(), userChat.getCreatedAt(), gptChat.getId(), gptChat.getCreatedAt(), gptMessage);
    }

    private void sendMessageToGroupUsers(List<Long> groupUserIds, GroupChatSocketDto socketDto){
        groupUserIds.forEach((targetId) -> socketServiceProvider.sendMessage(targetId, socketDto));
    }

    public DataListResDto<GroupChatResDto> getChatList(Long userId, String groupUuid, Long tapId, int page) {
        checkExistGroupUser(userId, groupUuid);
        checkExistTapInGroup(tapId, groupUuid);

        List<GroupChatResDto> chatList = groupChatRepository.getChatList(tapId, page);
        Collections.reverse(chatList);

        return new DataListResDto<>(page, chatList);
    }

    private Long findGroupIdByUuid(String groupUuid){
        return groupRepository.findGroupIdByGroupUuid(groupUuid)
                .orElseThrow(() -> new GroupException(NO_SUCH_GROUP));
    }

    private GroupUser findGroupUser(Long userId, String groupUuid){
        return groupRepository.findGroupUser(groupUuid, userId)
                .orElseThrow(() -> new GroupException(NOT_IN_GROUP));
    }

    private void checkValidContent(MultipartFile image, String message) {
        if(isInValidFile(image) && !StringUtils.hasText(message)){
            throw new GroupChatException(NO_CONTENT_IN_CHAT);
        }
    }

    private void checkExistTapInGroup(Long tapId, String groupUuid){
        if (!channelRepository.existsTapInGroup(tapId, groupUuid)){
            throw new ChannelException(NO_SUCH_TAP);
        }
    }

    private void checkExistGroupUser(Long userId, String groupUuid){
        if(!groupRepository.existUserInGroup(userId, groupUuid)){
            throw new GroupException(NOT_IN_GROUP);
        }
    }
}
