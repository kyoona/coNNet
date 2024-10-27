package houseInception.gptComm.service;

import houseInception.gptComm.domain.Friend;
import houseInception.gptComm.domain.FriendStatus;
import houseInception.gptComm.domain.User;
import houseInception.gptComm.dto.DataListResDto;
import houseInception.gptComm.dto.UserResDto;
import houseInception.gptComm.exception.FriendException;
import houseInception.gptComm.exception.UserException;
import houseInception.gptComm.repository.FriendRepository;
import houseInception.gptComm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static houseInception.gptComm.domain.FriendStatus.WAIT;
import static houseInception.gptComm.response.status.BaseErrorCode.*;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long requestFriend(Long userId, Long targetId) {
        User user = findUser(userId);
        User targetUser = findUser(targetId);

        checkAlreadyFriendRelation(userId, targetId);

        Friend friend = Friend.createFriend(user, targetUser);
        friendRepository.save(friend);

        return friend.getId();
    }

    @Transactional
    public Long acceptFriendRequest(Long userId, Long targetId) {
        checkExistUser(targetId);
        checkHasFriendRequest(targetId, userId);

        Friend friend = findFriend(targetId, userId, WAIT);
        friend.accept();

        return friend.getId();
    }

    @Transactional
    public Long denyFriendRequest(Long userId, Long targetId) {
        checkExistUser(targetId);
        checkHasFriendRequest(targetId, userId);

        Friend friend = findFriend(targetId, userId, WAIT);
        friendRepository.delete(friend);

        return friend.getId();
    }

    public DataListResDto<UserResDto> getFriendWaitList(Long userId) {
        List<UserResDto> requestSenders = friendRepository.findFriendRequestList(userId);

        return new DataListResDto<UserResDto>(0, requestSenders);
    }

    private void checkAlreadyFriendRelation(Long userId, Long targetId){
        if(friendRepository.existsFriend(userId, targetId)){
            throw new FriendException(ALREADY_FRIEND_REQUEST);
        }
    }

    private void checkHasFriendRequest(Long senderId, Long recipientId){
        if(!friendRepository.existsBySenderIdAndRecipientIdAndAcceptStatus(senderId, recipientId, WAIT)){
            throw new FriendException(NO_SUCH_FRIEND_REQUEST);
        }
    }

    private void checkExistUser(Long userId){
        if (!userRepository.existsById(userId)){
            throw new UserException(NO_SUCH_USER);
        }
    }

    private User findUser(Long userId){
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserException(NO_SUCH_USER);
        }

        return user;
    }

    private Friend findFriend(Long senderId, Long recipientId, FriendStatus acceptStatus){
        Friend friend = friendRepository.findBySenderIdAndRecipientIdAndAcceptStatus(senderId, recipientId, acceptStatus).orElse(null);
        if(friend == null){
            throw new FriendException(NO_SUCH_FRIEND);
        }

        return friend;
    }
}
