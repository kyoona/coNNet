package houseInception.connet.service;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.user.User;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.user.SettingUpdateDto;
import houseInception.connet.dto.user.UserProfileUpdateDto;
import houseInception.connet.repository.FriendRepository;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
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
    @Autowired
    EntityManager em;

    User user1;
    User user2;

    @BeforeEach
    void beforeEach(){
        user1 = User.create("user1", null, UUID.randomUUID().toString(), null);
        user2 = User.create("user2", null, UUID.randomUUID().toString(), null);
        em.persist(user1);
        em.persist(user2);
    }

    @AfterEach
    void afterEach(){
        userRepository.deleteAll();
    }

    @Test
    void getSelfProfile() {
        //when
        DefaultUserResDto result = userService.getSelfProfile(user1.getId());

        //then
        assertThat(result.getUserId()).isEqualTo(user1.getId());
        assertThat(result.getUserName()).isEqualTo(user1.getUserName());
    }

    @Test
    void getUserProfile() {
        //when
        DefaultUserResDto result = userService.getSelfProfile(user2.getId());

        //then
        assertThat(result.getUserId()).isEqualTo(user2.getId());
        assertThat(result.getUserName()).isEqualTo(user2.getUserName());
    }

    @Test
    void updateProfile() {
        //when
        UserProfileUpdateDto userProfileUpdateDto = new UserProfileUpdateDto("userName", "userDes", null);
        Long userId = userService.updateProfile(user1.getId(), userProfileUpdateDto);

        //then
        User testUser = userRepository.findById(userId).get();
        assertThat(testUser.getUserName()).isEqualTo(userProfileUpdateDto.getUserName());
        assertThat(testUser.getUserDescription()).isEqualTo(userProfileUpdateDto.getUserDescription());
    }

    @Test
    void deleteUser() {
        //when
        Long userId = userService.deleteUser(user1.getId());

        //then
        User testUser = userRepository.findById(userId).get();
        assertThat(testUser.getStatus()).isEqualTo(Status.DELETED);
    }

    @Test
    void updateSetting() {
        //when
        Long userId = userService.updateSetting(user1.getId(), new SettingUpdateDto(true));

        //then
        User testUser = userRepository.findById(userId).get();
        assertThat(testUser.getSetting().isAlarm()).isTrue();
    }
}