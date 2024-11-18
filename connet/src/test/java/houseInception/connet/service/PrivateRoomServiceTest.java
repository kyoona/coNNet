package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.domain.UserBlock;
import houseInception.connet.domain.UserBlockType;
import houseInception.connet.domain.privateRoom.PrivateChat;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;
import houseInception.connet.dto.PrivateChatAddDto;
import houseInception.connet.dto.PrivateChatAddRestDto;
import houseInception.connet.dto.PrivateRoomResDto;
import houseInception.connet.exception.PrivateRoomException;
import houseInception.connet.repository.PrivateRoomRepository;
import houseInception.connet.repository.UserBlockRepository;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class PrivateRoomServiceTest {

    @Autowired
    PrivateRoomService privateRoomService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PrivateRoomRepository privateRoomRepository;
    @Autowired
    UserBlockRepository userBlockRepository;

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

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
    }

    @Test
    void addPrivateChat_채팅방X_파일X() {
        //when
        String message = "mess1";
        PrivateChatAddDto chatAddDto = new PrivateChatAddDto(null, message, null);
        PrivateChatAddRestDto result = privateRoomService.addPrivateChat(user1.getId(), user2.getId(), chatAddDto);

        //then
        PrivateRoom privateRoom = privateRoomRepository.findPrivateRoomWithUser(result.getChatRoomUuid()).orElse(null);
        assertThat(privateRoom).isNotNull();

        List<PrivateRoomUser> privateRoomUsers = privateRoom.getPrivateRoomUsers();
        assertThat(privateRoomUsers.size()).isEqualTo(2);
        assertThat(privateRoomUsers.stream().map(PrivateRoomUser::getUser))
                .extracting("id").contains(user1.getId(), user2.getId());

        List<PrivateChat> privateChats = privateRoomRepository.findPrivateChatsInPrivateRoom(privateRoom.getId());
        assertThat(privateChats.size()).isEqualTo(1);
        assertThat(privateChats.get(0).getMessage()).isEqualTo(message);
    }

    @Test
    void addPrivateChat_채팅방O_파일X() {
        //given
        PrivateRoom privateRoom = PrivateRoom.create(user1, user2);
        privateRoomRepository.save(privateRoom);

        //when
        String message = "mess1";
        PrivateChatAddDto chatAddDto = new PrivateChatAddDto(privateRoom.getPrivateRoomUuid(), message, null);
        PrivateChatAddRestDto result = privateRoomService.addPrivateChat(user1.getId(), user2.getId(), chatAddDto);

        //then
        List<PrivateChat> privateChats = privateRoomRepository.findPrivateChatsInPrivateRoom(privateRoom.getId());
        assertThat(privateChats.size()).isEqualTo(1);
        assertThat(privateChats.get(0).getMessage()).isEqualTo(message);
    }

    @Test
    void addPrivateChat_차단된_유저() {
        //given
        UserBlock userBlock = UserBlock.create(user2, user1, UserBlockType.REQUEST);
        UserBlock reverseUserBlock = UserBlock.create(user1, user2, UserBlockType.ACCEPT);

        userBlockRepository.save(userBlock);
        userBlockRepository.save(reverseUserBlock);

        //when
        String message = "mess1";
        PrivateChatAddDto chatAddDto = new PrivateChatAddDto(null, message, null);
        assertThatThrownBy(() -> privateRoomService.addPrivateChat(user1.getId(), user2.getId(), chatAddDto)).isInstanceOf(PrivateRoomException.class);
    }

    @Test
    void getPrivateRoomList() {
        //given
        PrivateRoom privateRoom1 = PrivateRoom.create(user1, user2);
        PrivateRoom privateRoom2 = PrivateRoom.create(user1, user3);
        PrivateRoom privateRoom3 = PrivateRoom.create(user1, user4);

        privateRoomRepository.save(privateRoom1);
        privateRoomRepository.save(privateRoom2);
        privateRoomRepository.save(privateRoom3);

        privateRoom2.addUserToUserChat("message", null, privateRoom2.getPrivateRoomUsers().get(0));
        em.flush();
        privateRoom3.addUserToUserChat("message", null, privateRoom3.getPrivateRoomUsers().get(0));
        em.flush();
        privateRoom1.addUserToUserChat("message", null, privateRoom1.getPrivateRoomUsers().get(0));
        em.flush();

        //when
        List<PrivateRoomResDto> result = privateRoomService.getPrivateRoomList(user1.getId(), 1).getData();

        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).extracting("chatRoomId").containsExactly(privateRoom1.getId(), privateRoom3.getId(), privateRoom2.getId());
    }
}