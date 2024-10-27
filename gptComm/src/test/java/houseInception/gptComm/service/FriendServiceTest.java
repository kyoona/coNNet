package houseInception.gptComm.service;

import houseInception.gptComm.domain.Friend;
import houseInception.gptComm.domain.FriendStatus;
import houseInception.gptComm.domain.User;
import houseInception.gptComm.dto.DataListResDto;
import houseInception.gptComm.dto.UserResDto;
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

import java.util.List;
import java.util.NoSuchElementException;

import static houseInception.gptComm.domain.FriendStatus.ACCEPT;
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
    User user3;
    User user4;
    User user5;

    @BeforeEach
    void beforeEach(){
        user1 = User.create("user1", null, null, null);
        userRepository.save(user1);

        user2 = User.create("user2", null, null, null);
        userRepository.save(user2);

        user3 = User.create("user3", null, null, null);
        userRepository.save(user3);

        user4 = User.create("user4", null, null, null);
        userRepository.save(user4);

        user5 = User.create("user5", null, null, null);
        userRepository.save(user5);
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

        //when
        assertThatThrownBy(() -> friendService.requestFriend(user1.getId(), user2.getId())).isInstanceOf(FriendException.class);
    }

    @Test
    void requestFriend_상대방이_이미_요청() {
        //given
        Friend friend = Friend.createFriend(user1, user2);
        friendRepository.save(friend);

        //when
        assertThatThrownBy(() -> friendService.requestFriend(user2.getId(), user1.getId())).isInstanceOf(FriendException.class);
    }

    @Test
    void acceptFriendRequest() {
        //given
        Friend friend = Friend.createFriend(user1, user2);
        friendRepository.save(friend);

        //when
        Long friendId = friendService.acceptFriendRequest(user2.getId(), user1.getId());

        //then
        Friend findFriend = friendRepository.findById(friendId).orElse(null);
        assertThat(findFriend).isNotNull();
        assertThat(findFriend.getAcceptStatus()).isEqualTo(ACCEPT);
    }

    @Test
    void acceptFriendRequest_요청존재x() {
        //when
        assertThatThrownBy(() -> friendService.acceptFriendRequest(user2.getId(), user1.getId())).isInstanceOf(FriendException.class);
    }

    @Test
    void acceptFriendRequest_이미수락() {
        //given
        Friend friend = Friend.createFriend(user1, user2);
        friend.accept();
        friendRepository.save(friend);

        //when
        assertThatThrownBy(() -> friendService.acceptFriendRequest(user2.getId(), user1.getId())).isInstanceOf(FriendException.class);
    }

    @Test
    void denyFriendRequest() {
        //given
        Friend friend = Friend.createFriend(user1, user2);
        friendRepository.save(friend);

        //when
        Long friendId = friendService.denyFriendRequest(user2.getId(), user1.getId());

        //then
        assertThatThrownBy(() -> friendRepository.findById(friendId).orElseThrow()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void denyFriendRequest_요청존재x() {
        //when
        assertThatThrownBy(() -> friendService.denyFriendRequest(user2.getId(), user1.getId())).isInstanceOf(FriendException.class);
    }

    @Test
    void getFriendWaitList() {
        //given
        Friend friend1 = Friend.createFriend(user2, user1);
        friendRepository.save(friend1);

        Friend friend2 = Friend.createFriend(user3, user1);
        friendRepository.save(friend2);

        Friend friend3 = Friend.createFriend(user4, user1);
        friend3.accept();
        friendRepository.save(friend3);

        //when
        DataListResDto<UserResDto> result = friendService.getFriendWaitList(user1.getId());

        //then
        List<UserResDto> senderList = result.getData();
        assertThat(senderList.size()).isEqualTo(2);
        assertThat(senderList).extracting("userId").contains(user2.getId(), user3.getId());

    }

    @Test
    void getFriendList() {
        //given
        Friend friend1 = Friend.createFriend(user1, user2);
        friend1.accept();
        friendRepository.save(friend1);

        Friend friend2 = Friend.createFriend(user3, user1);
        friend2.accept();
        friendRepository.save(friend2);

        Friend friend3 = Friend.createFriend(user1, user4);
        friend3.accept();
        friendRepository.save(friend3);

        Friend friend4 = Friend.createFriend(user1, user5);
        friendRepository.save(friend4);

        //when
        DataListResDto<UserResDto> result = friendService.getFriendList(user1.getId());

        //then
        List<UserResDto> friendUserList = result.getData();
        assertThat(friendUserList.size()).isEqualTo(3);
        assertThat(friendUserList).extracting("userId").containsExactly(user2.getId(), user3.getId(), user4.getId());
    }
}