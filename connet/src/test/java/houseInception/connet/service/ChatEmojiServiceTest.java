package houseInception.connet.service;

import houseInception.connet.domain.*;
import houseInception.connet.domain.group.Group;
import houseInception.connet.domain.privateRoom.PrivateChat;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;
import houseInception.connet.dto.chatEmoji.EmojiDto;
import houseInception.connet.dto.chatEmoji.ChatEmojiUserResDto;
import houseInception.connet.exception.ChatEmojiException;
import houseInception.connet.exception.GroupException;
import houseInception.connet.exception.PrivateRoomException;
import houseInception.connet.repository.ChatEmojiRepository;
import houseInception.connet.repository.PrivateRoomRepository;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class ChatEmojiServiceTest {

    @Autowired
    ChatEmojiService chatEmojiService;
    @Autowired
    ChatEmojiRepository chatEmojiRepository;
    @Autowired
    PrivateRoomRepository privateRoomRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    User user1;
    User user2;
    User user3;

    @BeforeEach
    void beforeEach(){
        user1 = User.create("user1", null, null, null);
        user2 = User.create("user2", null, null, null);
        user3 = User.create("user3", null, null, null);
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
    }

    @AfterEach
    void afterEach(){
        userRepository.deleteAll();
    }

    @Test
    void addEmojiToPrivateChat() {
        //given
        PrivateRoom privateRoom = PrivateRoom.create(user1, user2);
        em.persist(privateRoom);

        PrivateRoomUser privateRoomUser1 = privateRoomRepository.findPrivateRoomUser(privateRoom.getId(), user1.getId()).orElseThrow();
        PrivateChat privateChat = privateRoom.addUserToUserChat("mess", null, privateRoomUser1);
        em.flush();

        //when
        EmojiDto emojiDto = new EmojiDto(EmojiType.HEART);
        Long emojiId = chatEmojiService.addEmojiToPrivateChat(user1.getId(), privateChat.getId(), emojiDto);

        //then
        ChatEmoji findChatEmoji = chatEmojiRepository.findById(emojiId).orElseThrow();
        assertThat(findChatEmoji.getChatId()).isEqualTo(privateChat.getId());
        assertThat(findChatEmoji.getUser().getId()).isEqualTo(user1.getId());
        assertThat(findChatEmoji.getChatRoomType()).isEqualTo(ChatRoomType.PRIVATE);
        assertThat(findChatEmoji.getEmojiType()).isEqualTo(EmojiType.HEART);
    }

    @Test
    void addEmojiToPrivateChat_중복() {
        //given
        PrivateRoom privateRoom = PrivateRoom.create(user1, user2);
        em.persist(privateRoom);

        PrivateRoomUser privateRoomUser1 = privateRoomRepository.findPrivateRoomUser(privateRoom.getId(), user1.getId()).orElseThrow();
        PrivateChat privateChat = privateRoom.addUserToUserChat("mess", null, privateRoomUser1);
        em.flush();

        ChatEmoji chatEmoji = ChatEmoji.createPrivateChatEmoji(user1, privateChat.getId(), EmojiType.HEART);
        em.persist(chatEmoji);

        //when
        EmojiDto emojiDto = new EmojiDto(EmojiType.HEART);
        assertThatThrownBy(() -> chatEmojiService.addEmojiToPrivateChat(user1.getId(), privateChat.getId(), emojiDto))
                .isInstanceOf(ChatEmojiException.class);
    }

    @Test
    void addEmojiToPrivateChat_채팅방_권한x() {
        //given
        PrivateRoom privateRoom = PrivateRoom.create(user1, user2);
        em.persist(privateRoom);

        PrivateRoomUser privateRoomUser1 = privateRoomRepository.findPrivateRoomUser(privateRoom.getId(), user1.getId()).orElseThrow();
        PrivateChat privateChat = privateRoom.addUserToUserChat("mess", null, privateRoomUser1);
        em.flush();

        //when
        EmojiDto emojiDto = new EmojiDto(EmojiType.HEART);
        assertThatThrownBy(() -> chatEmojiService.addEmojiToPrivateChat(user3.getId(), privateChat.getId(), emojiDto))
                .isInstanceOf(PrivateRoomException.class);
    }

    @Test
    void removeEmojiToPrivateChat() {
        //given
        PrivateRoom privateRoom = PrivateRoom.create(user1, user2);
        em.persist(privateRoom);

        PrivateRoomUser privateRoomUser1 = privateRoomRepository.findPrivateRoomUser(privateRoom.getId(), user1.getId()).orElseThrow();
        PrivateChat privateChat = privateRoom.addUserToUserChat("mess", null, privateRoomUser1);
        em.flush();

        ChatEmoji chatEmoji = ChatEmoji.createPrivateChatEmoji(user1, privateChat.getId(), EmojiType.HEART);
        em.persist(chatEmoji);

        //when
        chatEmojiService.removeEmojiToPrivateChat(user1.getId(), privateChat.getId(), new EmojiDto(EmojiType.HEART));

        //then
        assertThat(chatEmojiRepository.findById(chatEmoji.getId())).isEmpty();
    }

    @Test
    void removeEmojiToPrivateChat_권한X() {
        //given
        PrivateRoom privateRoom = PrivateRoom.create(user1, user2);
        em.persist(privateRoom);

        PrivateRoomUser privateRoomUser1 = privateRoomRepository.findPrivateRoomUser(privateRoom.getId(), user1.getId()).orElseThrow();
        PrivateChat privateChat = privateRoom.addUserToUserChat("mess", null, privateRoomUser1);
        em.flush();

        ChatEmoji chatEmoji = ChatEmoji.createPrivateChatEmoji(user1, privateChat.getId(), EmojiType.HEART);
        em.persist(chatEmoji);

        //when
        assertThatThrownBy(() -> chatEmojiService.removeEmojiToPrivateChat(user2.getId(), privateChat.getId(), new EmojiDto(EmojiType.HEART)))
                .isInstanceOf(ChatEmojiException.class);
    }

    @Test
    void getEmojiInfoInPrivateRoom() {
        //given
        PrivateRoom privateRoom = PrivateRoom.create(user1, user2);
        em.persist(privateRoom);

        PrivateRoomUser privateRoomUser1 = privateRoomRepository.findPrivateRoomUser(privateRoom.getId(), user1.getId()).orElseThrow();
        PrivateChat privateChat = privateRoom.addUserToUserChat("mess", null, privateRoomUser1);
        em.flush();

        ChatEmoji chatEmoji1 = ChatEmoji.createPrivateChatEmoji(user1, privateChat.getId(), EmojiType.HEART);
        ChatEmoji chatEmoji2 = ChatEmoji.createPrivateChatEmoji(user2, privateChat.getId(), EmojiType.HEART);
        em.persist(chatEmoji1);
        em.persist(chatEmoji2);

        //when
        List<ChatEmojiUserResDto> result = chatEmojiService.getEmojiInfoInPrivateRoom(user1.getId(), privateChat.getId(), EmojiType.HEART);

        //then
        assertThat(result)
                .extracting(ChatEmojiUserResDto::getUserName)
                .contains(user1.getUserName(), user2.getUserName());
    }

    @Test
    void addEmojiToGroupChat() {
        //given
        Group group = Group.create(user1, "group", null, null, 10, false);
        em.persist(group);

        GroupChat chat = GroupChat.createUserToUser(group.getId(), group.getGroupUserList().get(0), null, "mess", null);
        em.persist(chat);

        //when
        EmojiDto emojiDto = new EmojiDto(EmojiType.GREAT);
        Long emojiId = chatEmojiService.addEmojiToGroupChat(user1.getId(), chat.getId(), emojiDto);

        //then
        ChatEmoji chatEmoji = chatEmojiRepository.findById(emojiId).get();
        assertThat(chatEmoji.getEmojiType()).isEqualTo(emojiDto.getEmojiType());
        assertThat(chatEmoji.getChatRoomType()).isEqualTo(ChatRoomType.GROUP);
    }

    @Test
    void addEmojiToGroupChat_그룹멤버x() {
        //given
        Group group = Group.create(user1, "group", null, null, 10, false);
        Group group2 = Group.create(user2, "group", null, null, 10, false);
        em.persist(group);
        em.persist(group2);

        GroupChat chat = GroupChat.createUserToUser(group.getId(), group.getGroupUserList().get(0), null, "mess", null);
        em.persist(chat);

        //when
        assertThatThrownBy(() -> chatEmojiService.addEmojiToGroupChat(user2.getId(), chat.getId(), new EmojiDto(EmojiType.GREAT)))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void removeEmojiToGroupChat() {
        //given
        Group group = Group.create(user1, "group", null, null, 10, false);
        em.persist(group);

        GroupChat chat = GroupChat.createUserToUser(group.getId(), group.getGroupUserList().get(0), null, "mess", null);
        em.persist(chat);

        ChatEmoji emoji = ChatEmoji.createGroupChatEmoji(user1, chat.getId(), EmojiType.HEART);
        em.persist(emoji);

        //when
        Long emojiId = chatEmojiService.removeEmojiToGroupChat(user1.getId(), chat.getId(), new EmojiDto(EmojiType.HEART));

        //then
        assertThat(chatEmojiRepository.findById(emojiId)).isEmpty();
    }

    @Test
    void removeEmojiToGroupChat_이모지_등록유저x() {
        //given
        Group group = Group.create(user1, "group", null, null, 10, false);
        group.addUser(user2);
        em.persist(group);

        GroupChat chat = GroupChat.createUserToUser(group.getId(), group.getGroupUserList().get(0), null, "mess", null);
        em.persist(chat);

        ChatEmoji emoji = ChatEmoji.createGroupChatEmoji(user1, chat.getId(), EmojiType.HEART);
        em.persist(emoji);

        //when
        assertThatThrownBy(() -> chatEmojiService.removeEmojiToGroupChat(user2.getId(), chat.getId(), new EmojiDto(EmojiType.HEART)))
                .isInstanceOf(ChatEmojiException.class);
    }
}