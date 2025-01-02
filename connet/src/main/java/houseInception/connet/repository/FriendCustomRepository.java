package houseInception.connet.repository;

import houseInception.connet.dto.ActiveUserResDto;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.friend.FriendFilterDto;

import java.util.List;

public interface FriendCustomRepository {

    void deleteAllFriendsOfUser(Long userId);

    boolean existsFriendRequest(Long userId, Long targetId);

    List<ActiveUserResDto> getFriendList(Long userId, FriendFilterDto filterDto);
    List<DefaultUserResDto> getFriendWaitList(Long userId);
    List<DefaultUserResDto> getFriendRequestList(Long userId);
}
