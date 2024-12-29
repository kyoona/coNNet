package houseInception.connet.repository;

import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.dto.group.GroupResDto;
import houseInception.connet.dto.group.GroupUserResDto;

import java.util.List;
import java.util.Optional;

public interface GroupCustomRepository {

    Optional<GroupUser> findGroupUser(Long groupId, Long userId);
    Optional<GroupUser> findGroupUser(String groupUuid, Long userId);
    boolean existUserInGroup(Long userId, String groupUuid);
    boolean existGroupOwner(Long userId, String groupUuid);
    Long countOfGroupUsers(Long groupId);
    Optional<Long> findGroupIdByGroupUuid(String groupUuid);
    List<Long> findUserIdsOfGroupExceptUser(String groupUuid, Long userId);

    List<GroupUserResDto> getGroupUserList(String groupUuid);
    List<GroupResDto> getGroupList(Long userId, int page);
}
