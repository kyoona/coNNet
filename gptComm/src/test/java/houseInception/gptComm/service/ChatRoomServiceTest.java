package houseInception.gptComm.service;

import houseInception.gptComm.domain.User;
import houseInception.gptComm.domain.chatRoom.Chat;
import houseInception.gptComm.domain.chatRoom.ChatRoom;
import houseInception.gptComm.dto.ChatAddDto;
import houseInception.gptComm.dto.GptChatResDto;
import houseInception.gptComm.repository.ChatRoomRepository;
import houseInception.gptComm.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static houseInception.gptComm.domain.Status.ALIVE;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class ChatRoomServiceTest {

    @Autowired
    ChatRoomService chatRoomService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;

    User user1;

    @BeforeEach
    void beforeEach(){
        user1 = User.create("user1", null, null, null);
        userRepository.save(user1);
    }

    @AfterEach
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
}