package houseInception.gptComm.service;

import houseInception.gptComm.domain.Friend;
import houseInception.gptComm.domain.User;
import houseInception.gptComm.exception.FriendException;
import houseInception.gptComm.exception.UserException;
import houseInception.gptComm.repository.FriendRepository;
import houseInception.gptComm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.gptComm.response.status.BaseErrorCode.ALREADY_FRIEND_REQUEST;
import static houseInception.gptComm.response.status.BaseErrorCode.NO_SUCH_USER;

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

    private void checkAlreadyFriendRelation(Long userId, Long targetId){
        if(friendRepository.existsFriend(userId, targetId)){
            throw new FriendException(ALREADY_FRIEND_REQUEST);
        }
    }

    private User findUser(Long userId){
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserException(NO_SUCH_USER);
        }

        return user;
    }
}
