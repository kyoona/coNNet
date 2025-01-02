package houseInception.connet.repository;

import houseInception.connet.domain.group.Group;
import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.dto.group.*;
import houseInception.connet.dto.user.CommonGroupOfUserResDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GroupCustomRepository {

    Optional<Group> findGroupWithGroupUsers(Long groupId);
    Optional<Group> findGroupWithTags(String groupUuid);
    List<Group> findGroupListOfOwnerWithGroupUsers(Long userId);
    List<GroupUser> findGroupUserListOfNotOwnerWithGroup(Long userId);
    Optional<GroupUser> findGroupUser(Long groupId, Long userId);
    Optional<GroupUser> findGroupUser(String groupUuid, Long userId);

    boolean existUserInGroup(Long userId, String groupUuid);
    boolean existUserInGroup(Long userId, Long groupId);
    boolean existGroupOwner(Long userId, String groupUuid);
    Long countOfGroupUsers(Long groupId);
    Map<Long, Long> countOfGroupUsers(List<Long> groupId);
    Optional<Long> findGroupIdByGroupUuid(String groupUuid);
    List<Long> findUserIdsOfGroupExceptUser(String groupUuid, Long userId);

    List<GroupUserResDto> getGroupUserList(String groupUuid);
    List<GroupResDto> getGroupList(Long userId, int page);
    List<PublicGroupResDto> getPublicGroupList(Long userId, GroupFilter filter);
    GroupDetailResDto getGroupDetail(String groupUuid);
    List<CommonGroupOfUserResDto> getCommonGroupList(Long userId, Long targetId);
}
