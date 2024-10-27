package houseInception.gptComm.repository;

import houseInception.gptComm.dto.UserResDto;

import java.util.List;

public interface FriendCustomRepository {

    List<UserResDto> findFriendRequestList(Long userId);
    boolean existsFriend(Long userId, Long targetId);
}
