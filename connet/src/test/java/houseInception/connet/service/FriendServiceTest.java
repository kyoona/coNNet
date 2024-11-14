package houseInception.connet.service;

import houseInception.connet.domain.Friend;
import houseInception.connet.domain.User;
import houseInception.connet.dto.ActiveUserResDto;
import houseInception.connet.dto.DataListResDto;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.exception.FriendException;
import houseInception.connet.repository.FriendRepository;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static houseInception.connet.domain.FriendStatus.ACCEPT;
import static houseInception.connet.domain.FriendStatus.WAIT;
import static org.assertj.core.api.Assertions.*;

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
        friendService.requestFriend(user1.getId(), user2.getId());

        //then
        assertThat(friendRepository.existsBySenderIdAndReceiverIdAndAcceptStatus(user1.getId(), user2.getId(), WAIT)).isTrue();
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
    void requestFriend_스스로에게_친구_요청() {
        assertThatThrownBy(() -> friendService.requestFriend(user1.getId(), user1.getId())).isInstanceOf(FriendException.class);
    }

    @Test
    void acceptFriendRequest() {
        //given
        Friend friend = Friend.createFriend(user1, user2);
        friendRepository.save(friend);

        //when
        friendService.acceptFriendRequest(user2.getId(), user1.getId());

        //then
        assertThat(friendRepository.existsBySenderIdAndReceiverIdAndAcceptStatus(user1.getId(), user2.getId(), ACCEPT)).isTrue();
        assertThat(friendRepository.existsBySenderIdAndReceiverIdAndAcceptStatus(user2.getId(), user1.getId(), ACCEPT)).isTrue();
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
        DataListResDto<DefaultUserResDto> result = friendService.getFriendWaitList(user1.getId());

        //then
        List<DefaultUserResDto> senderList = result.getData();
        assertThat(senderList.size()).isEqualTo(2);
        assertThat(senderList).extracting("userId").contains(user2.getId(), user3.getId());

    }

    @Test
    void getFriendList() {
        //given
        Friend friendA1 = Friend.createFriend(user1, user2);
        friendA1.accept();
        friendRepository.save(friendA1);
        Friend friendA2 = Friend.createFriend(user2, user1);
        friendA2.accept();
        friendRepository.save(friendA2);

        Friend friendB1 = Friend.createFriend(user3, user1);
        friendB1.accept();
        friendRepository.save(friendB1);
        Friend friendB2 = Friend.createFriend(user1, user3);
        friendB2.accept();
        friendRepository.save(friendB2);

        Friend friendC1 = Friend.createFriend(user1, user4);
        friendC1.accept();
        friendRepository.save(friendC1);
        Friend friendC2 = Friend.createFriend(user4, user1);
        friendC2.accept();
        friendRepository.save(friendC2);

        Friend friendD1 = Friend.createFriend(user1, user5);
        friendRepository.save(friendD1);

        //when
        DataListResDto<ActiveUserResDto> result = friendService.getFriendList(user1.getId());

        //then
        List<ActiveUserResDto> friendUserList = result.getData();
        assertThat(friendUserList.size()).isEqualTo(3);
        assertThat(friendUserList).extracting("userId").containsExactly(user2.getId(), user3.getId(), user4.getId());
    }

    @Test
    void deleteFriend() {
        //given
        Friend friend1 = Friend.createFriend(user1, user2);
        friend1.accept();
        friendRepository.save(friend1);

        Friend friend2 = Friend.createFriend(user2, user1);
        friend2.accept();
        friendRepository.save(friend2);

        //when
        friendService.deleteFriend(user1.getId(), user2.getId());

        //then
        assertThat(friendRepository.existsBySenderIdAndReceiverIdAndAcceptStatus(user1.getId(), user2.getId(), ACCEPT)).isFalse();
        assertThat(friendRepository.existsBySenderIdAndReceiverIdAndAcceptStatus(user2.getId(), user1.getId(), ACCEPT)).isFalse();
    }
}