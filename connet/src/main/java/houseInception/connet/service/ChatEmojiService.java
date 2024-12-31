package houseInception.connet.service;

import houseInception.connet.domain.ChatEmoji;
import houseInception.connet.domain.ChatRoomType;
import houseInception.connet.domain.EmojiType;
import houseInception.connet.domain.User;
import houseInception.connet.dto.chatEmoji.EmojiDto;
import houseInception.connet.dto.chatEmoji.ChatEmojiUserResDto;
import houseInception.connet.exception.ChatEmojiException;
import houseInception.connet.exception.GroupChatException;
import houseInception.connet.exception.GroupException;
import houseInception.connet.exception.PrivateRoomException;
import houseInception.connet.repository.ChatEmojiRepository;
import houseInception.connet.repository.GroupChatRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.repository.PrivateRoomRepository;
import houseInception.connet.service.util.CommonDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static houseInception.connet.response.status.BaseErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatEmojiService {

    private final ChatEmojiRepository chatEmojiRepository;
    private final PrivateRoomRepository privateRoomRepository;
    private final GroupRepository groupRepository;
    private final GroupChatRepository groupChatRepository;
    private final CommonDomainService domainService;

    @Transactional
    public Long addEmojiToPrivateChat(Long userId, Long chatId, EmojiDto emojiDto){
        Long privateRoomId = checkExistsPrivateChatAndGetChatRoomID(chatId);
        checkUserInPrivateRoom(userId, privateRoomId);
        checkUserAlreadyHasEmoji(userId, chatId, emojiDto.getEmojiType(), ChatRoomType.PRIVATE);
        User user = domainService.findUser(userId);

        ChatEmoji chatEmoji = ChatEmoji.createPrivateChatEmoji(user, chatId, emojiDto.getEmojiType());
        chatEmojiRepository.save(chatEmoji);

        return chatEmoji.getId();
    }

    @Transactional
    public Long removeEmojiToPrivateChat(Long userId, Long chatId, EmojiDto emojiDto) {
        Long privateRoomId = checkExistsPrivateChatAndGetChatRoomID(chatId);
        checkUserInPrivateRoom(userId, privateRoomId);

        ChatEmoji chatEmoji = findChatEmoji(userId, chatId, emojiDto.getEmojiType(), ChatRoomType.PRIVATE);
        chatEmojiRepository.delete(chatEmoji);

        return chatEmoji.getId();
    }

    public List<ChatEmojiUserResDto> getEmojiInfoInPrivateRoom(Long userId, Long chatId, EmojiType emojiType) {
        Long privateRoomId = checkExistsPrivateChatAndGetChatRoomID(chatId);
        checkUserInPrivateRoom(userId, privateRoomId);

        List<ChatEmojiUserResDto> emojiUsers = chatEmojiRepository.getEmojiUsers(chatId, emojiType, ChatRoomType.PRIVATE);

        return emojiUsers;
    }

    @Transactional
    public Long addEmojiToGroupChat(Long userId, Long chatId, EmojiDto emojiDto) {
        Long groupIdOfChat = findGroupIdOfChat(chatId);
        checkUserInGroup(userId, groupIdOfChat);
        User user = domainService.findUser(userId);

        ChatEmoji chatEmoji = ChatEmoji.createGroupChatEmoji(user, chatId, emojiDto.getEmojiType());
        chatEmojiRepository.save(chatEmoji);

        return chatEmoji.getId();
    }

    @Transactional
    public Long removeEmojiToGroupChat(Long userId, Long chatId, EmojiDto emojiDto) {
        Long groupIdOfChat = findGroupIdOfChat(chatId);
        checkUserInGroup(userId, groupIdOfChat);

        ChatEmoji chatEmoji = findChatEmoji(userId, chatId, emojiDto.getEmojiType(), ChatRoomType.GROUP);
        chatEmojiRepository.delete(chatEmoji);

        return chatEmoji.getId();
    }

    public List<ChatEmojiUserResDto> getEmojiInfoInGroup(Long userId, Long chatId, EmojiType emojiType) {
        Long groupIdOfChat = findGroupIdOfChat(chatId);
        checkUserInGroup(userId, groupIdOfChat);

        List<ChatEmojiUserResDto> emojiUsers = chatEmojiRepository.getEmojiUsers(chatId, emojiType, ChatRoomType.GROUP);

        return emojiUsers;
    }

    private ChatEmoji findChatEmoji(Long userId, Long chatId, EmojiType emojiType, ChatRoomType chatRoomType) {
        return chatEmojiRepository.findChatEmoji(userId, chatId, emojiType, chatRoomType)
                .orElseThrow(() -> new ChatEmojiException(NO_SUCH_EMOJI));
    }

    private Long checkExistsPrivateChatAndGetChatRoomID(Long chatId){
        Long privateRoomId = privateRoomRepository.getPrivateRoomIdOfChat(chatId);
        if(privateRoomId == null){
            throw new PrivateRoomException(NO_SUCH_CHAT);
        }

        return privateRoomId;
    }

    private void checkUserAlreadyHasEmoji(Long userId, Long chatId, EmojiType emojiType, ChatRoomType chatRoomType) {
        if(chatEmojiRepository.existsEmojiInChat(userId, chatId, emojiType, chatRoomType)){
            throw new ChatEmojiException(ALREADY_HAS_EMOJI);
        }
    }

    private void checkUserInPrivateRoom(Long userId, Long privateRoomId) {
        if (!privateRoomRepository.existsAlivePrivateRoomUser(userId, privateRoomId)){
            throw new PrivateRoomException(NOT_CHATROOM_USER);
        }
    }

    private Long findGroupIdOfChat(Long chatId){
        return groupChatRepository.findGroupIdOfChat(chatId)
                .orElseThrow(() -> new GroupChatException(NO_SUCH_CHAT));
    }

    private void checkUserInGroup(Long userId, Long groupId){
        if (!groupRepository.existUserInGroup(userId, groupId)) {
            throw new GroupException(NOT_IN_GROUP);
        }
    }
}
