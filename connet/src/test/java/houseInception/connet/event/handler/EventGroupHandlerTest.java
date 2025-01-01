package houseInception.connet.event.handler;

import houseInception.connet.domain.GroupInvite;
import houseInception.connet.domain.user.User;
import houseInception.connet.domain.group.Group;
import houseInception.connet.repository.GroupInviteRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.service.GroupInviteService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EventGroupHandlerTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    GroupInviteRepository groupInviteRepository;
    @Autowired
    GroupInviteService groupInviteService;
    @Autowired
    EntityManager em;

    User groupOwner;
    User user1;
    Group group;

    @BeforeEach
    void beforeEach(){
        groupOwner = User.create("groupOwner", null, null, null);
        user1 = User.create("user1", null, null, null);
        userRepository.save(groupOwner);
        userRepository.save(user1);

        group = Group.create(groupOwner, "group", null, null, 3, false);
        groupRepository.save(group);
    }

    @AfterEach
    void afterEach(){
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void acceptGroupInvite() {
        //given
        GroupInvite groupInvite1 = GroupInvite.create(group.getGroupUuid(), groupOwner, user1);
        groupInviteRepository.save(groupInvite1);

        //when
        groupInviteService.acceptInvite(user1.getId(), group.getGroupUuid());

        //then
        assertThat(groupRepository.existUserInGroup(user1.getId(), group.getGroupUuid())).isTrue();
    }
}