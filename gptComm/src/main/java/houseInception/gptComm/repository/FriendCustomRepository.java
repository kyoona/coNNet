package houseInception.gptComm.repository;

import houseInception.gptComm.domain.Friend;
import houseInception.gptComm.dto.UserResDto;

import java.util.List;
import java.util.Optional;

public interface FriendCustomRepository {

    Optional<Friend> findFriendSenderOrReceiver(Long userId1, Long userId2);
    List<Friend> findFriendListWithUser(Long userId);

    List<UserResDto> findFriendRequestList(Long userId);

    boolean existsFriend(Long userId, Long targetId);
}
