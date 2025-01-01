package houseInception.connet.service;

import houseInception.connet.domain.GroupInvite;
import houseInception.connet.domain.user.User;
import houseInception.connet.domain.group.Group;
import houseInception.connet.dto.group_invite.GroupInviteDto;
import houseInception.connet.dto.group_invite.GroupInviteResDto;
import houseInception.connet.exception.GroupInviteException;
import houseInception.connet.repository.GroupInviteRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Slf4j
@Transactional
@SpringBootTest
class GroupInviteServiceTest {

    @Autowired
    GroupInviteService groupInviteService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    GroupInviteRepository groupInviteRepository;
    @Autowired
    EntityManager em;

    User groupOwner;
    User user1;
    User user2;
    
    Group group;

    @BeforeEach
    void beforeEach(){
        groupOwner = User.create("groupOwner", null, null, null);
        user1 = User.create("user1", null, null, null);
        user2 = User.create("user2", null, null, null);

        em.persist(groupOwner);
        em.persist(user1);
        em.persist(user2);
        
        group = Group.create(groupOwner, "group", null, null, 3, false);
        em.persist(group);
    }
    
    @AfterEach
    void afterEach(){
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void inviteGroup() {
        //when
        GroupInviteDto groupInviteDto = new GroupInviteDto(user1.getId());
        Long groupInviteId = groupInviteService.inviteGroup(groupOwner.getId(), group.getGroupUuid(), groupInviteDto);

        //then
        GroupInvite groupInvite = groupInviteRepository.findById(groupInviteId).orElseThrow();
        assertThat(groupInvite.getGroupUuid()).isEqualTo(group.getGroupUuid());
        assertThat(groupInvite.getInvitee().getId()).isEqualTo(user1.getId());
    }

    @Test
    void inviteGroup_이미_초대() {
        //given
        GroupInvite groupInvite = GroupInvite.create(group.getGroupUuid(), groupOwner, user1);
        em.persist(groupInvite);

        //when
        GroupInviteDto groupInviteDto = new GroupInviteDto(user1.getId());
        assertThatThrownBy(() -> groupInviteService.inviteGroup(groupOwner.getId(), group.getGroupUuid(), groupInviteDto))
                .isInstanceOf(GroupInviteException.class);
    }

    @Test
    void acceptInvite() {
        //given
        GroupInvite groupInvite = GroupInvite.create(group.getGroupUuid(), groupOwner, user1);
        em.persist(groupInvite);

        //when
        groupInviteService.acceptInvite(user1.getId(), group.getGroupUuid());

        //then
        assertThat(groupInviteRepository.findById(groupInvite.getId())).isEmpty();
    }

    @Test
    void acceptInvite_초대_받지_않음() {
        //given
        GroupInvite groupInvite = GroupInvite.create(group.getGroupUuid(), groupOwner, user1);
        em.persist(groupInvite);

        //when
        assertThatThrownBy(() -> groupInviteService.acceptInvite(user2.getId(), group.getGroupUuid()))
                .isInstanceOf(GroupInviteException.class);
    }

    @Test
    void denyInvite() {
        //given
        GroupInvite groupInvite = GroupInvite.create(group.getGroupUuid(), groupOwner, user1);
        em.persist(groupInvite);

        //when
        groupInviteService.denyInvite(user1.getId(), group.getGroupUuid());

        //then
        assertThat(groupInviteRepository.findById(groupInvite.getId())).isEmpty();
    }

    @Test
    void denyInvite_초대_받지_않음() {
        //given
        GroupInvite groupInvite = GroupInvite.create(group.getGroupUuid(), groupOwner, user1);
        em.persist(groupInvite);

        //when
        assertThatThrownBy(() -> groupInviteService.acceptInvite(user2.getId(), group.getGroupUuid()))
                .isInstanceOf(GroupInviteException.class);
    }

    @Test
    void getGroupInviteList() {
        //given
        Group group1 = Group.create(groupOwner, "group1", null, null, 3, false);
        Group group2 = Group.create(user1, "group2", null, null, 3, false);
        em.persist(group1);
        em.persist(group2);

        GroupInvite groupInvite1 = GroupInvite.create(group1.getGroupUuid(), groupOwner, user2);
        GroupInvite groupInvite2 = GroupInvite.create(group2.getGroupUuid(), user1, user2);
        em.persist(groupInvite1);
        em.persist(groupInvite2);

        //when
        List<GroupInviteResDto> result = groupInviteService.getGroupInviteList(user2.getId());

        //then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(GroupInviteResDto::getGroupName)
                .contains(group1.getGroupName(), group2.getGroupName());
        assertThat(result)
                .extracting((res) -> res.getInviter().getUserId())
                .contains(groupOwner.getId(), user1.getId());
    }

}