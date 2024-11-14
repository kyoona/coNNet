package houseInception.connet.repository;

import houseInception.connet.dto.ActiveUserResDto;
import houseInception.connet.dto.DefaultUserResDto;

import java.util.List;

public interface FriendCustomRepository {

    List<ActiveUserResDto> findFriendListWithUser(Long userId);

    List<DefaultUserResDto> findFriendRequestList(Long userId);

    boolean existsFriendRequest(Long userId, Long targetId);
}
