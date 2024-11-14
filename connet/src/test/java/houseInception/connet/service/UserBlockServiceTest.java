package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.repository.UserBlockRepository;
import houseInception.connet.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserBlockServiceTest {

    @Autowired
    UserBlockService userBlockService;

    @Autowired
    UserBlockRepository userBlockRepository;
    @Autowired
    UserRepository userRepository;

    User user1;
    User user2;

    @BeforeEach
    void beforeEach(){
        user1 = User.create("user1", null, null, null);
        userRepository.save(user1);

        user2 = User.create("user2", null, null, null);
        userRepository.save(user2);
    }

    @Test
    void blockUser() {
        //when
        userBlockService.blockUser(user1.getId(), user2.getId());

        //then
        assertThat(userBlockRepository.existsByUserIdAndTargetId(user1.getId(), user2.getId())).isTrue();
        assertThat(userBlockRepository.existsByUserIdAndTargetId(user2.getId(), user1.getId())).isTrue();
    }
}