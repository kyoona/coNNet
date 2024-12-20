package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.domain.group.Group;
import houseInception.connet.domain.group.GroupTag;
import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.dto.group.GroupAddDto;
import houseInception.connet.exception.GroupException;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class GroupServiceTest {

    @Autowired
    GroupService groupService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;
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
    void addGroup() {
        //when
        String groupName = "group";
        List<String> tags = List.of("tag1", "tag2", "tag3");
        GroupAddDto groupAddDto = new GroupAddDto(
                groupName,
                null,
                "new group",
                tags,
                10,
                true
        );

        String groupUuid = groupService.addGroup(user1.getId(), groupAddDto);

        //then
        Group group = groupRepository.findByGroupUuid(groupUuid).orElseThrow();
        assertThat(group.getGroupName()).isEqualTo(groupName);

        List<GroupUser> groupUserList = group.getGroupUserList();
        assertThat(groupUserList).hasSize(1);
        assertThat(groupUserList)
                .extracting(GroupUser::isOwner)
                .contains(true);


        List<GroupTag> groupTags = group.getGroupTagList();
        assertThat(groupTags).hasSize(3);
        assertThat(groupTags)
                .extracting(GroupTag::getTagName)
                .contains(tags.get(0), tags.get(1), tags.get(2));
    }

    @Test
    void addGroup_유효하지_않은_태그() {
        //when
        String groupName = "group";
        List<String> tags = List.of("t a g1");
        GroupAddDto groupAddDto = new GroupAddDto(
                groupName,
                null,
                "new group",
                tags,
                10,
                true
        );

        assertThatThrownBy(() -> groupService.addGroup(user1.getId(), groupAddDto))
                .isInstanceOf(GroupException.class);
    }
}