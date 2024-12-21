package houseInception.connet.repository;


import houseInception.connet.dto.group.GroupUserResDto;

import java.util.List;

public interface GroupCustomRepository {

    boolean existUserInGroup(Long userId, String groupUuid);
    Long countOfGroupUsers(Long groupId);

    List<GroupUserResDto> getGroupUserList(String groupUuid);
}
