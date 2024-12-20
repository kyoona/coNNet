package houseInception.connet.service;

import houseInception.connet.domain.Friend;
import houseInception.connet.domain.User;
import houseInception.connet.dto.friend.FriendType;
import houseInception.connet.dto.UserResDto;
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
}