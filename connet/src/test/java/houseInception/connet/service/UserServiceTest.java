package houseInception.connet.service;

import houseInception.connet.domain.Friend;
import houseInception.connet.domain.User;
import houseInception.connet.dto.FriendType;
import houseInception.connet.dto.UserResDto;
import houseInception.connet.repository.FriendRepository;
import houseInception.connet.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    FriendRepository friendRepository;

    User user1;
    User user2;

    @BeforeEach
    void beforeEach(){
        user1 = User.create("user1", null, UUID.randomUUID().toString(), null);
        userRepository.save(user1);

        user2 = User.create("user2", null, UUID.randomUUID().toString(), null);
        userRepository.save(user2);
    }

    @AfterEach
    void afterEach(){
        userRepository.deleteAll();
    }

    @Test
    void getUserInfo_본인검색() {
        //when
        UserResDto result = userService.getUserInfo(user1.getId(), null);

        //then
        assertThat(result.getUserId()).isEqualTo(user1.getId());
    }

    @Test
    void getUserInfo_타인검색_친구x() {
        //when
        UserResDto result = userService.getUserInfo(user1.getId(), user2.getEmail());

        //then
        assertThat(result.getUserId()).isEqualTo(user2.getId());
        assertThat(result.getFriendType()).isEqualTo(FriendType.NONE);
    }

    @Test
    void getUserInfo_타인검색_친구요청o() {
        //given
        Friend friend = Friend.createFriend(user1, user2);
        friendRepository.save(friend);

        //when
        UserResDto result = userService.getUserInfo(user1.getId(), user2.getEmail());

        //then
        assertThat(result.getUserId()).isEqualTo(user2.getId());
        assertThat(result.getFriendType()).isEqualTo(FriendType.REQUEST);
    }

    @Test
    void getUserInfo_타인검색_친구o() {
        //give
        Friend friend = Friend.createFriend(user1, user2);
        friend.accept();
        friendRepository.save(friend);

        //when
        UserResDto result = userService.getUserInfo(user1.getId(), user2.getEmail());

        //then
        assertThat(result.getUserId()).isEqualTo(user2.getId());
        assertThat(result.getFriendType()).isEqualTo(FriendType.FRIEND);
    }
}