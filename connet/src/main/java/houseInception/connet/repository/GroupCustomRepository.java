package houseInception.connet.repository;


import houseInception.connet.dto.group.GroupUserResDto;

import java.util.List;

public interface GroupCustomRepository {

    boolean existUserInGroup(Long userId, String groupUuid);

    List<GroupUserResDto> getGroupUserList(String groupUuid);
}
