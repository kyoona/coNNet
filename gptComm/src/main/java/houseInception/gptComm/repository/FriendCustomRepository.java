package houseInception.gptComm.repository;

import houseInception.gptComm.domain.Friend;
import houseInception.gptComm.dto.UserResDto;

import java.util.List;

public interface FriendCustomRepository {

    List<UserResDto> findFriendRequestList(Long userId);
    List<Friend> findFriendListWithUser(Long userId);
    boolean existsFriend(Long userId, Long targetId);
}
