package houseInception.gptComm.service;

import houseInception.gptComm.domain.User;
import houseInception.gptComm.dto.ChatAddDto;
import houseInception.gptComm.dto.GptChatResDto;
import houseInception.gptComm.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ChatRoomServiceTest {

    @Autowired
    ChatRoomService chatRoomService;

    @Autowired
    UserRepository userRepository;

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
    void addGptChat() {
        //given
        String message = "GPT모델 중 가장 싼 모델은 뭐야?";
        ChatAddDto chatAddDto = new ChatAddDto(null, message);

        //when
        GptChatResDto result = chatRoomService.addGptChat(user1.getId(), chatAddDto);

        //then
        System.out.println("result = " + result);
    }
}