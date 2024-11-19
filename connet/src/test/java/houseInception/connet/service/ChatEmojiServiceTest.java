package houseInception.connet.service;

import houseInception.connet.domain.ChatEmoji;
import houseInception.connet.domain.ChatRoomType;
import houseInception.connet.domain.EmojiType;
import houseInception.connet.domain.User;
import houseInception.connet.domain.privateRoom.PrivateChat;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;
import houseInception.connet.dto.EmojiAddDto;
import houseInception.connet.exception.ChatEmojiException;
import houseInception.connet.repository.ChatEmojiRepository;
import houseInception.connet.repository.PrivateRoomRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void addEmojiToPrivateChat() {
        //given
        PrivateRoom privateRoom = PrivateRoom.create(user1, user2);
        em.persist(privateRoom);

        PrivateRoomUser privateRoomUser1 = privateRoomRepository.findPrivateRoomUser(privateRoom.getId(), user1.getId()).orElseThrow();
        PrivateChat privateChat = privateRoom.addUserToUserChat("mess", null, privateRoomUser1);
        em.flush();

        //when
        EmojiAddDto emojiAddDto = new EmojiAddDto(EmojiType.HEART);
        Long emojiId = chatEmojiService.addEmojiToPrivateChat(user1.getId(), privateChat.getId(), emojiAddDto);

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
        EmojiAddDto emojiAddDto = new EmojiAddDto(EmojiType.HEART);
        assertThatThrownBy(() -> chatEmojiService.addEmojiToPrivateChat(user1.getId(), privateChat.getId(), emojiAddDto)).isInstanceOf(ChatEmojiException.class);
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
        EmojiAddDto emojiAddDto = new EmojiAddDto(EmojiType.HEART);
        assertThatThrownBy(() -> chatEmojiService.addEmojiToPrivateChat(user3.getId(), privateChat.getId(), emojiAddDto)).isInstanceOf(ChatEmojiException.class);
    }
}