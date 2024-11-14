package houseInception.connet.repository;

import houseInception.connet.domain.Friend;
import houseInception.connet.dto.DefaultUserResDto;

import java.util.List;
import java.util.Optional;

public interface FriendCustomRepository {

    List<Friend> findFriendListWithUser(Long userId);

    List<DefaultUserResDto> findFriendRequestList(Long userId);

    boolean existsFriendRequest(Long userId, Long targetId);
}
