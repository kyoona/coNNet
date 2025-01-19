package houseInception.connet.service;

import houseInception.connet.domain.GroupChat;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.user.User;
import houseInception.connet.domain.group.Group;
import houseInception.connet.domain.group.GroupTag;
import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.dto.group.*;
import houseInception.connet.exception.GroupException;
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
        Group group = groupRepository.findByGroupUuidAndStatus(groupUuid, Status.ALIVE).orElseThrow();
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

    @Test
    void updateGroup() {
        //given
        Group group = Group.create(user1, "groupName", null, null, 3, true);
        group.addTag(List.of("tag1", "tag2", "deleteTag1", "deleteTag2"));
        em.persist(group);

        //when
        List<String> addedTags = List.of("addTag1", "addTag2");
        List<String> deletedTags = List.of("deleteTag1", "deleteTag2");
        GroupUpdateDto updateDto = new GroupUpdateDto("update!", null, "des", addedTags, deletedTags, false);
        groupService.updateGroup(user1.getId(), group.getGroupUuid(), updateDto);

        //then
        Group testGroup = groupRepository.findGroupWithTags(group.getGroupUuid()).get();
        assertThat(testGroup.getGroupName()).isEqualTo(updateDto.getGroupName());
        assertThat(testGroup.getGroupDescription()).isEqualTo(updateDto.getGroupDescription());
        assertThat(testGroup.getGroupTagList()).hasSize(4);
        assertThat(testGroup.getGroupTagList())
                .extracting(GroupTag::getTagName)
                .containsExactlyInAnyOrder("tag1", "tag2", addedTags.get(0), addedTags.get(1));
    }

    @Test
    void updateGroup_방장x() {
        //given
        Group group = Group.create(user2, "groupName", null, null, 3, true);
        em.persist(group);

        //when
        GroupUpdateDto updateDto = new GroupUpdateDto("update!", null, "des", List.of(), List.of(), false);
        assertThatThrownBy(() -> groupService.updateGroup(user1.getId(), group.getGroupUuid(), updateDto))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void updateGroup_태그10개_초과() {
        //given
        Group group = Group.create(user2, "groupName", null, null, 3, true);
        group.addTag(List.of("tag1", "tag2", "deleteTag1", "deleteTag2"));
        em.persist(group);

        //when
        List<String> addedTags = List.of("addTag1", "addTag2", "addTag3", "addTag4", "addTag5", "addTag6", "addTag7", "addTag8", "addTag9", "addTag10");
        List<String> deletedTags = List.of("deleteTag1", "deleteTag2");
        GroupUpdateDto updateDto = new GroupUpdateDto("update!", null, "des", addedTags, deletedTags, false);
        assertThatThrownBy(() -> groupService.updateGroup(user1.getId(), group.getGroupUuid(), updateDto))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void getGroupUserList() {
        //given
        Group group = Group.create(user2, "groupName", null, null, 3, true);
        group.addUser(user1);
        group.addUser(user3);
        group.addUser(user4);
        em.persist(group);

        //when
        List<GroupUserResDto> result = groupService.getGroupUserList(user1.getId(), group.getGroupUuid());

        //then
        assertThat(result).hasSize(4);
        assertThat(result.get(0).getUserId()).isEqualTo(user2.getId()); //방장은 맨 첫번째로 조회
    }

    @Test
    void getGroupUserList_권한x() {
        //given
        Group group = Group.create(user1, "groupName", null, null, 3, true);
        em.persist(group);

        //when
        assertThatThrownBy(() -> groupService.getGroupUserList(user2.getId(), group.getGroupUuid()))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void enterGroup() {
        //given
        Group group = Group.create(user1, "groupName", null, null, 3, true);
        em.persist(group);

        //when
        groupService.enterGroup(user2.getId(), group.getGroupUuid());

        //then
        assertThat(groupRepository.existUserInGroup(user2.getId(), group.getGroupUuid())).isTrue();
    }

    @Test
    void enterGroup_그룹_인원_제한_초과() {
        //given
        Group group = Group.create(user1, "groupName", null, null, 1, true);
        em.persist(group);

        //when
        assertThatThrownBy(() -> groupService.enterGroup(user2.getId(), group.getGroupUuid()))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void enterGroup_private그룹() {
        //given
        Group group = Group.create(user1, "groupName", null, null, 3, false);
        em.persist(group);

        //when
        assertThatThrownBy(() -> groupService.enterGroup(user2.getId(), group.getGroupUuid()))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void exitGroup() {
        //given
        Group group = Group.create(user1, "groupName", null, null, 3, false);
        group.addUser(user2);
        em.persist(group);

        //when
        groupService.exitGroup(user2.getId(), group.getGroupUuid());

        //then
        assertThat(groupRepository.existUserInGroup(user2.getId(), group.getGroupUuid())).isFalse();
    }

    @Test
    void exitGroup_방장() {
        //given
        Group group = Group.create(user1, "groupName", null, null, 3, false);
        group.addUser(user2);
        em.persist(group);

        //when
        groupService.exitGroup(user1.getId(), group.getGroupUuid());

        //then
        assertThat(group.getStatus()).isEqualTo(Status.DELETED);
        assertThat(groupRepository.existUserInGroup(user1.getId(), group.getGroupUuid())).isFalse();
        assertThat(groupRepository.existUserInGroup(user2.getId(), group.getGroupUuid())).isFalse();
    }

    @Test
    void exitGroup_그룹에_속하지_않는_유저() {
        //given
        Group group = Group.create(user1, "groupName", null, null, 3, false);
        em.persist(group);

        //when
        assertThatThrownBy(() -> groupService.exitGroup(user2.getId(), group.getGroupUuid()))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void getGroupList() {
        //given
        Group group1 = Group.create(user1, "groupName", null, null, 3, false);
        Group group2 = Group.create(user1, "groupName", null, null, 3, false);
        em.persist(group1);
        em.persist(group2);

        //when
        List<GroupResDto> result = groupService.getGroupList(user1.getId(), 1).getData();

        //then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(GroupResDto::getGroupUuid)
                .containsExactly(group2.getGroupUuid(), group1.getGroupUuid());
    }

    @Test
    void getGroupList_그룹퇴장시_조회x() {
        //given
        Group group1 = Group.create(user1, "groupName", null, null, 3, false);
        Group group2 = Group.create(user1, "groupName", null, null, 3, false);
        Group group3 = Group.create(user2, "groupName", null, null, 3, false);
        group3.addUser(user1);
        em.persist(group1);
        em.persist(group2);
        em.persist(group3);

        groupService.exitGroup(user1.getId(), group3.getGroupUuid());

        //when
        List<GroupResDto> result = groupService.getGroupList(user1.getId(), 1).getData();

        //then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(GroupResDto::getGroupUuid)
                .containsExactly(group2.getGroupUuid(), group1.getGroupUuid());
    }

    @Test
    void getPublicGroupList() {
        //given
        Group group1 = Group.create(user1, "groupName", null, null, 10, true);
        List<String> group1Tags = List.of("tag1", "tag2", "tag3");
        group1.addTag(group1Tags);

        Group group2 = Group.create(user2, "groupName", null, null, 9, false);

        Group group3 = Group.create(user3, "groupName", null, null, 8, true);
        group3.addUser(user2);

        em.persist(group1);
        em.persist(group2);
        em.persist(group3);

        GroupChat group2Chat1 = GroupChat.createUserToUser(group2.getId(), group2.getGroupUserList().get(0), null, "message", null);
        em.persist(group2Chat1);

        //when
        List<PublicGroupResDto> result = groupService.getPublicGroupList(user1.getId(), new GroupFilter(1, null)).getData();

        //then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(PublicGroupResDto::getGroupId)
                .contains(group1.getId(), group3.getId());
        assertThat(result)
                .extracting(PublicGroupResDto::isParticipate)
                .contains(true, false);
        assertThat(result)
                .extracting(PublicGroupResDto::getUserCount)
                .contains(1, 2);
    }

    @Test
    void getPublicGroupList_필터링() {
        //given
        Group group1 = Group.create(user1, "groupName", null, null, 10, true);
        List<String> group1Tags = List.of("filter", "tag2", "tag3");
        group1.addTag(group1Tags);

        Group group2 = Group.create(user2, "groupName", null, null, 9, true);
        List<String> group2Tags = List.of("tag1", "tag2", "tag3");
        group2.addTag(group2Tags);

        Group group3 = Group.create(user3, "filterGroup", null, null, 8, true);

        em.persist(group1);
        em.persist(group2);
        em.persist(group3);

        //when
        GroupFilter filter = new GroupFilter(1, "filter");
        List<PublicGroupResDto> result = groupService.getPublicGroupList(user1.getId(), filter).getData();

        //then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(PublicGroupResDto::getGroupId)
                .contains(group1.getId(), group3.getId());
    }

    @Test
    void getGroupDetail() {
        //given
        Group group = Group.create(user1, "groupName", null, null, 10, true);
        List<String> group1Tags = List.of("filter", "tag2", "tag3");
        group.addTag(group1Tags);
        em.persist(group);

        //when
        GroupDetailResDto result = groupService.getGroupDetail(user1.getId(), group.getGroupUuid());

        //then
        assertThat(result.getTags()).hasSize(3);
    }

    @Test
    void getGroupDetail_그룹_멤버x() {
        //given
        Group group = Group.create(user1, "groupName", null, null, 10, true);
        em.persist(group);

        //when
        assertThatThrownBy(() -> groupService.getGroupDetail(user2.getId(), group.getGroupUuid()))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void exitGroupsOfUser() {
        //given
        Group group1 = Group.create(user1, "group1", null, null, 10, true);
        group1.addUser(user2);
        group1.addUser(user3);

        Group group2 = Group.create(user2, "group2", null, null, 10, true);
        group2.addUser(user1);

        em.persist(group1);
        em.persist(group2);

        //when
        groupService.exitGroupsOfUser(user1.getId());

        //then
        Group testGroup1 = groupRepository.findById(group1.getId()).get();
        assertThat(testGroup1.getStatus()).isEqualTo(Status.DELETED);
        assertThat(testGroup1.getGroupUserList())
                .extracting(GroupUser::getStatus)
                .containsExactly(Status.DELETED, Status.DELETED, Status.DELETED);

        Group testGroup2 = groupRepository.findById(group2.getId()).get();
        assertThat(testGroup2.getStatus()).isEqualTo(Status.ALIVE);
        assertThat(testGroup2.getGroupUserList())
                .extracting(GroupUser::getStatus)
                .contains(Status.ALIVE, Status.DELETED);
    }
}