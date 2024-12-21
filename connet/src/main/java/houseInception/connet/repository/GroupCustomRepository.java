package houseInception.connet.repository;

import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.dto.group.GroupUserResDto;

import java.util.List;
import java.util.Optional;

public interface GroupCustomRepository {

    Optional<GroupUser> findGroupUser(Long groupId, Long userId);
    boolean existUserInGroup(Long userId, String groupUuid);
    boolean existGroupOwner(Long userId, Long groupId);
    Long countOfGroupUsers(Long groupId);
    Optional<Long> findGroupIdByGroupUuid(String groupUuid);

    List<GroupUserResDto> getGroupUserList(String groupUuid);
}
