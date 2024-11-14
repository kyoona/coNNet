package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.domain.UserBlock;
import houseInception.connet.domain.UserBlockType;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.repository.UserBlockRepository;
import houseInception.connet.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static houseInception.connet.domain.UserBlockType.ACCEPT;
import static houseInception.connet.domain.UserBlockType.REQUEST;
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
    User user3;
    User user4;

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
    }

    @Test
    void blockUser() {
        //when
        userBlockService.blockUser(user1.getId(), user2.getId());

        //then
        assertThat(userBlockRepository.existsByUserIdAndTargetId(user1.getId(), user2.getId())).isTrue();
        assertThat(userBlockRepository.existsByUserIdAndTargetId(user2.getId(), user1.getId())).isTrue();
    }

    @Test
    void getBlockUserList() {
        //given
        UserBlock userBlock1 = UserBlock.create(user1, user2, REQUEST);
        userBlockRepository.save(userBlock1);

        UserBlock userBlock2 = UserBlock.create(user1, user3, REQUEST);
        userBlockRepository.save(userBlock2);

        UserBlock userBlock3 = UserBlock.create(user1, user4, ACCEPT);
        userBlockRepository.save(userBlock3);

        //when
        List<DefaultUserResDto> result = userBlockService.getBlockUserList(user1.getId()).getData();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting("userId").contains(user2.getId(), user3.getId());
    }
}