package houseInception.connet.service;

import houseInception.connet.domain.user.User;
import houseInception.connet.domain.UserBlock;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.repository.UserBlockRepository;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static houseInception.connet.domain.UserBlockType.ACCEPT;
import static houseInception.connet.domain.UserBlockType.REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class UserBlockServiceTest {

    @Autowired
    UserBlockService userBlockService;

    @Autowired
    UserBlockRepository userBlockRepository;
    @Autowired
    UserRepository userRepository;
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

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(user4);
    }

    @AfterEach
    void afterEach(){
        userRepository.deleteAll();
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
    void blockUser_양방향() {
        //given
        UserBlock userBlock = UserBlock.create(user1, user2, REQUEST);
        UserBlock reverseUserBlock = UserBlock.create(user2, user1, ACCEPT);
        em.persist(userBlock);
        em.persist(reverseUserBlock);

        //when
        userBlockService.blockUser(user2.getId(), user1.getId());

        //then
        assertThat(userBlockRepository.existsByUserIdAndTargetIdAndBlockType(user1.getId(), user2.getId(), REQUEST)).isTrue();
        assertThat(userBlockRepository.existsByUserIdAndTargetIdAndBlockType(user2.getId(), user1.getId(), REQUEST)).isTrue();
    }

    @Test
    void cancelBlock_단방향_차단중() {
        //given
        UserBlock userBlock = UserBlock.create(user1, user2, REQUEST);
        UserBlock reverseUserBlock = UserBlock.create(user2, user1, ACCEPT);
        em.persist(userBlock);
        em.persist(reverseUserBlock);

        //when
        userBlockService.cancelBlock(user1.getId(), user2.getId());

        //then
        assertThat(userBlockRepository.existsByUserIdAndTargetId(user1.getId(), user2.getId())).isFalse();
        assertThat(userBlockRepository.existsByUserIdAndTargetId(user2.getId(), user1.getId())).isFalse();
    }

    @Test
    void cancelBlock_양방향_차단중() {
        //given
        UserBlock userBlock = UserBlock.create(user1, user2, REQUEST);
        UserBlock reverseUserBlock = UserBlock.create(user2, user1, REQUEST);
        em.persist(userBlock);
        em.persist(reverseUserBlock);

        //when
        userBlockService.cancelBlock(user1.getId(), user2.getId());

        //then
        assertThat(userBlockRepository.existsByUserIdAndTargetIdAndBlockType(user1.getId(), user2.getId(), ACCEPT)).isTrue();
        assertThat(userBlockRepository.existsByUserIdAndTargetIdAndBlockType(user2.getId(), user1.getId(), REQUEST)).isTrue();
    }

    @Test
    void getBlockUserList() {
        //given
        UserBlock userBlock1 = UserBlock.create(user1, user2, REQUEST);
        UserBlock userBlock2 = UserBlock.create(user1, user3, REQUEST);
        UserBlock userBlock3 = UserBlock.create(user1, user4, ACCEPT);
        em.persist(userBlock1);
        em.persist(userBlock2);
        em.persist(userBlock3);

        //when
        List<DefaultUserResDto> result = userBlockService.getBlockUserList(user1.getId()).getData();

        //then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(DefaultUserResDto::getUserId)
                .contains(user2.getId(), user3.getId());
    }

    @Test
    void deleteAllUserBlockOfUser() {
        //given
        UserBlock userBlock = UserBlock.create(user1, user2, REQUEST);
        UserBlock reverseUserBlock = UserBlock.create(user2, user1, REQUEST);
        em.persist(userBlock);
        em.persist(reverseUserBlock);

        //when
        userBlockService.deleteAllUserBlockOfUser(user1.getId());

        //then
        assertThat(userBlockRepository.existsByUserIdAndTargetId(user1.getId(), user2.getId())).isFalse();
        assertThat(userBlockRepository.existsByUserIdAndTargetId(user2.getId(), user1.getId())).isFalse();
    }
}