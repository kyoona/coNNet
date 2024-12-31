package houseInception.connet.repository;

import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.dto.group.GroupFilter;
import houseInception.connet.dto.group.GroupResDto;
import houseInception.connet.dto.group.GroupUserResDto;
import houseInception.connet.dto.group.PublicGroupResDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GroupCustomRepository {

    Optional<GroupUser> findGroupUser(Long groupId, Long userId);
    Optional<GroupUser> findGroupUser(String groupUuid, Long userId);
    boolean existUserInGroup(Long userId, String groupUuid);
    boolean existGroupOwner(Long userId, String groupUuid);
    Long countOfGroupUsers(Long groupId);
    Map<Long, Long> countOfGroupUsers(List<Long> groupId);
    Optional<Long> findGroupIdByGroupUuid(String groupUuid);
    List<Long> findUserIdsOfGroupExceptUser(String groupUuid, Long userId);

    List<GroupUserResDto> getGroupUserList(String groupUuid);
    List<GroupResDto> getGroupList(Long userId, int page);
    List<PublicGroupResDto> getPublicGroupList(Long userId, GroupFilter filter);
}
