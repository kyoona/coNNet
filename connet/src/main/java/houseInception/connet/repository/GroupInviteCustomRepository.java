package houseInception.connet.repository;

import houseInception.connet.dto.group_invite.GroupInviteResDto;

import java.util.List;

public interface GroupInviteCustomRepository {

    List<GroupInviteResDto> getGroupInviteListOfUser(Long userId);
}
