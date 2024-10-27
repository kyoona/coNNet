package houseInception.gptComm.service;

import houseInception.gptComm.domain.User;
import houseInception.gptComm.domain.chatRoom.Chat;
import houseInception.gptComm.domain.chatRoom.ChatRoom;
import houseInception.gptComm.domain.chatRoom.ChatRoomUser;
import houseInception.gptComm.dto.*;
import houseInception.gptComm.exception.ChatRoomException;
import houseInception.gptComm.repository.ChatRoomRepository;
import houseInception.gptComm.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static houseInception.gptComm.domain.Status.ALIVE;
import static houseInception.gptComm.domain.Status.DELETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class ChatRoomServiceTest {

    @Autowired
    ChatRoomService chatRoomService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    EntityManager em;

    User user1;
    User user2;

    @BeforeEach
    void beforeEach(){
        user1 = User.create("user1", null, null, null);
        userRepository.save(user1);

        user2 = User.create("user2", null, null, null);
        userRepository.save(user2);
    }

//    @AfterEach
    void afterEach(){
        userRepository.deleteAll();
    }

    @Test
    void addGptChat_새채팅방() {
        //given
        String message = "GPT모델 중 가장 싼 모델은 뭐야?";
        ChatAddDto chatAddDto = new ChatAddDto(null, message);

        //when
        GptChatResDto result = chatRoomService.addGptChat(user1.getId(), chatAddDto);

        //then
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomUuidAndStatus(result.getChatRoomUuid(), ALIVE).orElse(null);
        assertThat(result.getChatRoomUuid()).isEqualTo(chatRoom.getChatRoomUuid());
        assertThat(result.getTitle()).isEqualTo(chatRoom.getTitle());

        List<Chat> chatList = chatRoomRepository.getChatListOfChatRoom(chatRoom.getId());
        assertThat(chatList.size()).isEqualTo(2);
        assertThat(chatList).extracting("id").containsExactly(result.getUserChatId(), result.getGptChatId());
    }

    @Test
    void addGptChat_기존채팅방() {
        //given
        ChatRoom chatRoom = ChatRoom.createGptRoom(user1);
        chatRoomRepository.save(chatRoom);

        String message = "GPT모델 중 가장 싼 모델은 뭐야?";
        ChatAddDto chatAddDto = new ChatAddDto(chatRoom.getChatRoomUuid(), message);

        //when
        GptChatResDto result = chatRoomService.addGptChat(user1.getId(), chatAddDto);

        //then
        ChatRoom findChatRoom = chatRoomRepository.findByChatRoomUuidAndStatus(result.getChatRoomUuid(), ALIVE).orElse(null);
        assertThat(findChatRoom.getChatRoomUuid()).isEqualTo(chatRoom.getChatRoomUuid());
        assertThat(findChatRoom.getTitle()).isEqualTo(chatRoom.getTitle());

        List<Chat> chatList = chatRoomRepository.getChatListOfChatRoom(findChatRoom.getId());
        assertThat(chatList.size()).isEqualTo(2);
    }

    @Test
    void addGptChat_기존채팅방_권한X() {
        //given
        User newUser = User.create("newUser", null, null, null);
        userRepository.save(newUser);

        ChatRoom chatRoom = ChatRoom.createGptRoom(user1);
        chatRoomRepository.save(chatRoom);

        String message = "GPT모델 중 가장 싼 모델은 뭐야?";
        ChatAddDto chatAddDto = new ChatAddDto(chatRoom.getChatRoomUuid(), message);

        //when
        assertThatThrownBy(() -> chatRoomService.addGptChat(newUser.getId(), chatAddDto)).isInstanceOf(ChatRoomException.class);
    }

    @Test
    void getGptChatRoomList() {
        //given
        ChatRoom chatRoom1 = ChatRoom.createGptRoom(user1);
        chatRoomRepository.save(chatRoom1);

        ChatRoom chatRoom2 = ChatRoom.createGptRoom(user1);
        chatRoomRepository.save(chatRoom2);

        User newUser = User.create("newUser", null, null, null);
        userRepository.save(newUser);

        ChatRoom newUserChatRoom = ChatRoom.createGptRoom(newUser);
        chatRoomRepository.save(newUserChatRoom);

        //when
        DataListResDto<GptChatRoomListResDto> result = chatRoomService.getGptChatRoomList(user1.getId(), 1);

        //then
        List<GptChatRoomListResDto> data = result.getData();
        assertThat(data.size()).isEqualTo(2);
        assertThat(data).extracting("chatRoomUuid").containsExactly(chatRoom2.getChatRoomUuid(), chatRoom1.getChatRoomUuid());

    }

    @Test
    void updateChatRoom() {
        //given
        ChatRoom chatRoom = ChatRoom.createGptRoom(user1);
        chatRoomRepository.save(chatRoom);

        //when
        String title = "new title";
        chatRoomService.updateChatRoom(user1.getId(), chatRoom.getChatRoomUuid(), title);

        //then
        ChatRoom findChatRoom = chatRoomRepository.findById(chatRoom.getId()).orElse(null);
        assertThat(findChatRoom).isNotNull();
        assertThat(findChatRoom.getTitle()).isEqualTo(title);
    }

    @Test
    void updateChatRoom_권한X() {
        //given
        ChatRoom chatRoom = ChatRoom.createGptRoom(user1);
        chatRoomRepository.save(chatRoom);

        //when
        String title = "new title";
        assertThatThrownBy(() -> chatRoomService.updateChatRoom(user2.getId(), chatRoom.getChatRoomUuid(), title)).isInstanceOf(ChatRoomException.class);
    }

    @Test
    void deleteChatRoom() {
        //given
        ChatRoom chatRoom = ChatRoom.createGptRoom(user1);
        chatRoomRepository.save(chatRoom);

        //when
        chatRoomService.deleteChatRoom(user1.getId(), chatRoom.getChatRoomUuid());

        //then
        ChatRoom findChatRoom = chatRoomRepository.findById(chatRoom.getId()).orElse(null);
        assertThat(findChatRoom).isNotNull();
        assertThat(findChatRoom.getStatus()).isEqualTo(DELETED);
    }

    @Test
    void deleteChatRoom_권한X() {
        //given
        ChatRoom chatRoom = ChatRoom.createGptRoom(user1);
        chatRoomRepository.save(chatRoom);

        User newUser = User.create("newUser", null, null, null);
        userRepository.save(newUser);

        //when
        assertThatThrownBy(() -> chatRoomService.deleteChatRoom(newUser.getId(), chatRoom.getChatRoomUuid())).isInstanceOf(ChatRoomException.class);
    }

    @Test
    void getGptChatRoomChatList() {
        //given
        ChatRoom chatRoom = ChatRoom.createGptRoom(user1);
        chatRoomRepository.save(chatRoom);

        ChatRoomUser chatRoomUser1 = chatRoom.getChatRoomUsers().get(0);
        Chat chat1 = chatRoom.addUserChatToGpt(chatRoomUser1, "content1");
        Chat chat2 = chatRoom.addGptChat("I am GPT 1");
        Chat chat3 = chatRoom.addUserChatToGpt(chatRoomUser1, "content2");

        em.flush();

        //when
        GptChatRoomChatListResDto result = chatRoomService.getGptChatRoomChatList(user1.getId(), chatRoom.getChatRoomUuid(), 1);

        //then
        List<GptChatRoomChatResDto> messages = result.getMessages();
        assertThat(messages.size()).isEqualTo(3);
        assertThat(messages).extracting("chatId").containsExactly(chat3.getId(), chat2.getId(), chat1.getId());
        assertThat(messages).extracting("writer").containsNull();
    }

    @Test
    void getGptChatRoomChatList_권한X() {
        //given
        ChatRoom chatRoom = ChatRoom.createGptRoom(user1);
        chatRoomRepository.save(chatRoom);

        User newUser = User.create("newUser", null, null, null);
        userRepository.save(newUser);

        //when
        assertThatThrownBy(() -> chatRoomService.getGptChatRoomChatList(newUser.getId(), chatRoom.getChatRoomUuid(), 1)).isInstanceOf(ChatRoomException.class);
    }
}