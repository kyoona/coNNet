package houseInception.connet.service;

import houseInception.connet.domain.*;
import houseInception.connet.domain.privateRoom.PrivateChat;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;
import houseInception.connet.domain.user.User;
import houseInception.connet.dto.chatEmoji.ChatEmojiResDto;
import houseInception.connet.dto.privateRoom.*;
import houseInception.connet.exception.PrivateRoomException;
import houseInception.connet.exception.SocketException;
import houseInception.connet.exception.UserBlockException;
import houseInception.connet.repository.PrivateRoomRepository;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.socketManager.SocketManager;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@Transactional
@SpringBootTest
class PrivateRoomServiceTest {

    @Autowired
    PrivateRoomService privateRoomService;
    @Autowired
    PrivateRoomRepository privateRoomRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SocketManager socketManager;

    @Autowired
    EntityManager em;

    User user1;
    User user2;
    User user3;
    User user4;

    @BeforeEach
    void beforeEach(){
        user1 = User.create("user1", null, null, null);
        user2 = User.create("user2", null, null, null);
        user3 = User.create("user3", null, null, null);
        user4 = User.create("user4", null, null, null);
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(user4);

        socketManager.addSocket(user1.getId(), Mockito.mock(WebSocketSession.class));
        socketManager.addSocket(user2.getId(), Mockito.mock(WebSocketSession.class));
        socketManager.addSocket(user3.getId(), Mockito.mock(WebSocketSession.class));
        socketManager.addSocket(user4.getId(), Mockito.mock(WebSocketSession.class));
    }

    @AfterEach
    void afterEach(){
        userRepository.deleteAll();
    }

    @Test
    void addPrivateChat_채팅방_생성_파일X() {
        //when
        String message = "mess1";
        PrivateChatAddDto chatAddDto = new PrivateChatAddDto(message, null);
        PrivateChatAddResDto result = privateRoomService.addPrivateChat(user1.getId(), user2.getId(), chatAddDto);

        //then
        PrivateRoom privateRoom = privateRoomRepository.findPrivateRoomWithUser(result.getChatRoomUuid()).orElseThrow();
        List<PrivateRoomUser> privateRoomUsers = privateRoom.getPrivateRoomUsers();
        assertThat(privateRoomUsers).hasSize(2);
        assertThat(privateRoomUsers)
                .extracting(roomUser -> roomUser.getUser().getId())
                .containsExactlyInAnyOrder(user1.getId(), user2.getId());


        List<PrivateChat> privateChats = privateRoomRepository.findPrivateChatsInPrivateRoom(privateRoom.getId());
        assertThat(privateChats).hasSize(1);
        assertThat(privateChats.get(0).getMessage()).isEqualTo(message);
    }

    @Test
    void addPrivateChat_채팅방_존재() {
        //given
        PrivateRoom privateRoom = PrivateRoom.create(user1, user2);
        em.persist(privateRoom);

        //when
        PrivateChatAddDto chatAddDto = new PrivateChatAddDto("mess1", null);
        PrivateChatAddResDto result = privateRoomService.addPrivateChat(user1.getId(), user2.getId(), chatAddDto);

        //then
        assertThat(result.getChatRoomUuid()).isEqualTo(privateRoom.getPrivateRoomUuid());
    }

    @Test
    void addPrivateChat_차단된_유저() {
        //given
        UserBlock userBlock = UserBlock.create(user2, user1, UserBlockType.REQUEST);
        UserBlock reverseUserBlock = UserBlock.create(user1, user2, UserBlockType.ACCEPT);

        em.persist(userBlock);
        em.persist(reverseUserBlock);

        //when
        PrivateChatAddDto chatAddDto = new PrivateChatAddDto("mess1", null);
        assertThatThrownBy(() -> privateRoomService.addPrivateChat(user1.getId(), user2.getId(), chatAddDto))
                .isInstanceOf(UserBlockException.class);
        assertThatThrownBy(() -> privateRoomService.addPrivateChat(user2.getId(), user1.getId(), chatAddDto))
                .isInstanceOf(UserBlockException.class);
    }

    @Test
    void addPrivateChat_소켓연결x() {
        //given
        User noSocketUser = User.create("noSocketUser", null, null, null);
        em.persist(noSocketUser);

        //when
        PrivateChatAddDto chatAddDto = new PrivateChatAddDto("mess1", null);
        assertThatThrownBy(() -> privateRoomService.addPrivateChat(noSocketUser.getId(), user2.getId(), chatAddDto))
                .isInstanceOf(SocketException.class);
    }

    @Test
    void addGptChat_채팅방_존재() {
        //given
        PrivateRoom privateRoom = PrivateRoom.create(user1, user2);
        em.persist(privateRoom);

        //when
        String message = "한국의 위도 경도를 알려줘";
        GptPrivateChatAddResDto result = privateRoomService.addGptChat(user1.getId(), user2.getId(), message);

        //then
        PrivateChat userChat = privateRoomRepository.findPrivateChatsById(result.getUserChatId()).orElseThrow();
        assertThat(userChat.getMessage()).isEqualTo(message);

        PrivateChat gptChat = privateRoomRepository.findPrivateChatsById(result.getGptChatId()).orElseThrow();
        assertThat(gptChat.getMessage()).isEqualTo(result.getMessage());
        log.info("gpt response = {}", result.getMessage());
    }

    @Test
    void addGptChat_이전_응답_기반() {
        //given
        PrivateRoom privateRoom = PrivateRoom.create(user1, user2);
        em.persist(privateRoom);

        //when
        GptPrivateChatAddResDto res1 = privateRoomService.addGptChat(user1.getId(), user2.getId(), "Playstation 4 pro는 언제 출시됐어?");
        GptPrivateChatAddResDto res2 = privateRoomService.addGptChat(user1.getId(), user2.getId(), "달러 기준으로 얼마야?");
        GptPrivateChatAddResDto res3 = privateRoomService.addGptChat(user1.getId(), user2.getId(), "한국 원화 기준으로 바꿔줄래");

        //then
        log.info(res1.getMessage());
        log.info(res2.getMessage());
        log.info(res3.getMessage());
    }

    @Test
    void getPrivateRoomList() {
        //given
        PrivateRoom privateRoom1 = PrivateRoom.create(user1, user2);
        PrivateRoom privateRoom2 = PrivateRoom.create(user1, user3);
        PrivateRoom privateRoom3 = PrivateRoom.create(user1, user4);
        em.persist(privateRoom1);
        em.persist(privateRoom2);
        em.persist(privateRoom3);

        privateRoom2.addUserToUserChat("message", null, privateRoom2.getPrivateRoomUsers().get(0));
        em.flush();
        privateRoom3.addUserToUserChat("message", null, privateRoom3.getPrivateRoomUsers().get(0));
        em.flush();
        privateRoom1.addUserToUserChat("message", null, privateRoom1.getPrivateRoomUsers().get(0));
        em.flush();

        UserBlock userBlock = UserBlock.create(user1, user4, UserBlockType.REQUEST);
        em.persist(userBlock);

        //when
        List<PrivateRoomResDto> result = privateRoomService.getPrivateRoomList(user1.getId(), 1).getData();

        //then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(PrivateRoomResDto::getChatRoomId)
                .containsExactly(privateRoom1.getId(), privateRoom3.getId(), privateRoom2.getId());
        assertThat(result)
                .extracting(PrivateRoomResDto::isBlock)
                .containsExactly(false, true, false);
    }

    @Test
    void getPrivateRoomList_채팅읽었는지_유무() {
        //given
        PrivateRoom privateRoomA = PrivateRoom.create(user1, user2);
        PrivateRoom privateRoomB = PrivateRoom.create(user1, user3);
        em.persist(privateRoomA);
        em.persist(privateRoomB);

        PrivateChat chatA1 = privateRoomA.addUserToUserChat("message", null, privateRoomA.getPrivateRoomUsers().get(0));
        PrivateChat chatA2 = privateRoomA.addUserToUserChat("message", null, privateRoomA.getPrivateRoomUsers().get(0));
        PrivateChat chatB1 = privateRoomB.addUserToUserChat("message", null, privateRoomB.getPrivateRoomUsers().get(0));
        em.flush();

        ChatReadLog readLog1 = ChatReadLog.createPrivateChatLog(user1.getId(), privateRoomA.getPrivateRoomUuid(), chatA1.getId());
        ChatReadLog readLog2 = ChatReadLog.createPrivateChatLog(user1.getId(), privateRoomB.getPrivateRoomUuid(), chatB1.getId());
        em.persist(readLog1);
        em.persist(readLog2);

        //when
        List<PrivateRoomResDto> result = privateRoomService.getPrivateRoomList(user1.getId(), 1).getData();

        //then
        assertThat(result)
                .extracting(PrivateRoomResDto::isUnread)
                .containsExactlyInAnyOrder(true, false);
    }

    @Test
    void getPrivateChatList_이모지o() {
        //given
        PrivateRoom privateRoom1 = PrivateRoom.create(user1, user2);
        em.persist(privateRoom1);
        PrivateChat privateChat1 = privateRoom1.addUserToUserChat("message1", null, privateRoom1.getPrivateRoomUsers().get(0));
        PrivateChat privateChat2 = privateRoom1.addUserToUserChat("message2", null, privateRoom1.getPrivateRoomUsers().get(1));
        em.flush();

        ChatEmoji chatEmoji1 = ChatEmoji.createPrivateChatEmoji(user1, privateChat1.getId(), EmojiType.HEART);
        ChatEmoji chatEmoji2 = ChatEmoji.createPrivateChatEmoji(user2, privateChat1.getId(), EmojiType.HEART);
        ChatEmoji chatEmoji3 = ChatEmoji.createPrivateChatEmoji(user2, privateChat1.getId(), EmojiType.CHECK);
        em.persist(chatEmoji1);
        em.persist(chatEmoji2);
        em.persist(chatEmoji3);

        //when
        List<PrivateChatResDto> result = privateRoomService.getPrivateChatList(user1.getId(), user2.getId(), 1).getData();

        //then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(PrivateChatResDto::getChatId)
                .containsExactly(privateChat2.getId(), privateChat1.getId());

        List<ChatEmojiResDto> emojiList = result.get(1).getEmoji();
        assertThat(emojiList)
                .extracting(ChatEmojiResDto::getEmojiType)
                .contains(EmojiType.HEART, EmojiType.CHECK);
        assertThat(emojiList)
                .extracting(ChatEmojiResDto::getCount)
                .contains(1, 2);
    }

    @Test
    void deletePrivateRoom() {
        //given
        PrivateRoom privateRoom1 = PrivateRoom.create(user1, user2);
        em.persist(privateRoom1);

        //when
        privateRoomService.deletePrivateRoom(user1.getId(), privateRoom1.getPrivateRoomUuid());

        //then
        PrivateRoomUser privateRoomUser = privateRoomRepository.findPrivateRoomUser(privateRoom1.getId(), user1.getId()).orElseThrow();
        assertThat(privateRoomUser.getStatus()).isEqualTo(Status.DELETED);
    }

    @Test
    void deletePrivateRoom_권한x() {
        //given
        PrivateRoom privateRoom1 = PrivateRoom.create(user1, user2);
        em.persist(privateRoom1);

        //when
        assertThatThrownBy(() -> privateRoomService.deletePrivateRoom(user3.getId(), privateRoom1.getPrivateRoomUuid()))
                .isInstanceOf(PrivateRoomException.class);
    }

    @Test
    void 채팅방_퇴장후_목록_조회() {
        //given
        String chatRoomUuid1 = privateRoomService.addPrivateChat(user1.getId(), user2.getId(), new PrivateChatAddDto("message", null)).getChatRoomUuid();
        String chatRoomUuid2 = privateRoomService.addPrivateChat(user1.getId(), user3.getId(), new PrivateChatAddDto("message", null)).getChatRoomUuid();
        String chatRoomUuid3 = privateRoomService.addPrivateChat(user1.getId(), user4.getId(), new PrivateChatAddDto("message", null)).getChatRoomUuid();
        privateRoomService.deletePrivateRoom(user1.getId(), chatRoomUuid1);

        //when
        List<PrivateRoomResDto> result = privateRoomService.getPrivateRoomList(user1.getId(), 1).getData();

        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(PrivateRoomResDto::getChatRoomUuid)
                .contains(chatRoomUuid2, chatRoomUuid3);
    }

    @Test
    void 채팅방_퇴장후_재입장_채팅목록_조회() {
        //given
        String chatRoomUuid1 = privateRoomService.addPrivateChat(user1.getId(), user2.getId(), new PrivateChatAddDto("message", null)).getChatRoomUuid();
        privateRoomService.deletePrivateRoom(user1.getId(), chatRoomUuid1);
        privateRoomService.addPrivateChat(user2.getId(), user1.getId(), new PrivateChatAddDto("message", null));

        //when
        List<PrivateChatResDto> data1 = privateRoomService.getPrivateChatList(user1.getId(), user2.getId(), 1).getData();
        List<PrivateChatResDto> data2 = privateRoomService.getPrivateChatList(user2.getId(), user1.getId(), 1).getData();

        //then
        assertThat(data1).hasSize(1);
        assertThat(data2).hasSize(2);
    }
}