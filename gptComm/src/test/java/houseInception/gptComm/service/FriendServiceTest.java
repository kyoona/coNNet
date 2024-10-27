package houseInception.gptComm.service;

import houseInception.gptComm.domain.Friend;
import houseInception.gptComm.domain.FriendStatus;
import houseInception.gptComm.domain.User;
import houseInception.gptComm.exception.FriendException;
import houseInception.gptComm.repository.FriendRepository;
import houseInception.gptComm.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AssertionsKt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.gptComm.domain.FriendStatus.WAIT;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class FriendServiceTest {

    @Autowired
    FriendService friendService;

    @Autowired
    FriendRepository friendRepository;
    @Autowired
    UserRepository userRepository;

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

    @AfterEach
    void afterEach(){
        friendRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void requestFriend() {
        //when
        Long friendId = friendService.requestFriend(user1.getId(), user2.getId());

        //then
        Friend friend = friendRepository.findById(friendId).orElse(null);
        assertThat(friend).isNotNull();
        assertThat(friend.getSender().getId()).isEqualTo(user1.getId());
        assertThat(friend.getRecipient().getId()).isEqualTo(user2.getId());
        assertThat(friend.getAcceptStatus()).isEqualTo(WAIT);
    }

    @Test
    void requestFriend_이미_요청() {
        //given
        Friend friend = Friend.createFriend(user1, user2);
        friendRepository.save(friend);
        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> friendService.requestFriend(user1.getId(), user2.getId())).isInstanceOf(FriendException.class);
    }

    @Test
    void requestFriend_상대방이_이미_요청() {
        //given
        Friend friend = Friend.createFriend(user1, user2);
        friendRepository.save(friend);
        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> friendService.requestFriend(user2.getId(), user1.getId())).isInstanceOf(FriendException.class);
    }
}