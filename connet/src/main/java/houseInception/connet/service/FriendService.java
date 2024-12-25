package houseInception.connet.service;

import houseInception.connet.domain.Friend;
import houseInception.connet.domain.FriendStatus;
import houseInception.connet.domain.User;
import houseInception.connet.dto.ActiveUserResDto;
import houseInception.connet.dto.DataListResDto;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.friend.FriendFilterDto;
import houseInception.connet.exception.FriendException;
import houseInception.connet.repository.FriendRepository;
import houseInception.connet.service.util.CommonDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static houseInception.connet.domain.FriendStatus.ACCEPT;
import static houseInception.connet.domain.FriendStatus.WAIT;
import static houseInception.connet.response.status.BaseErrorCode.*;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final CommonDomainService domainService;

    @Transactional
    public Long requestFriendById(Long userId, Long targetId) {
        User targetUser = domainService.findUser(targetId);
        domainService.checkNotUserBlock(userId, targetId);
        User user = domainService.findUser(userId);

        if(userId.equals(targetId)){
            throw new FriendException(CANT_NOT_REQUEST_SELF);
        }

        checkAlreadyFriendRequestOfTwoWay(userId, targetId);

        Friend friend = Friend.createFriend(user, targetUser);
        friendRepository.save(friend);

        return friend.getId();
    }

    @Transactional
    public Long requestFriendByEmail(Long userId, String email) {
        User targetUser = domainService.findUserByEmail(email);
        domainService.checkNotUserBlock(userId, targetUser.getId());
        User user = domainService.findUser(userId);

        if(userId.equals(targetUser.getId())){
            throw new FriendException(CANT_NOT_REQUEST_SELF);
        }

        checkAlreadyFriendRequestOfTwoWay(userId, targetUser.getId());

        Friend friend = Friend.createFriend(user, targetUser);
        friendRepository.save(friend);

        return friend.getId();
    }

    @Transactional
    public Long cancelFriendRequest(Long userId, Long targetId){
        domainService.checkExistUser(targetId);

        Friend friend = findFriend(userId, targetId, WAIT);
        friendRepository.delete(friend);

        return friend.getId();
    }

    @Transactional
    public Long acceptFriendRequest(Long userId, Long targetId) {
        User targetUser = domainService.findUser(targetId);
        checkHasFriendRequestOfOneWay(targetId, userId);
        User user = domainService.findUser(userId);

        Friend requestFriend = findFriend(targetId, userId, WAIT);
        requestFriend.accept();

        Friend friend = Friend.createFriend(user, targetUser);
        friend.accept();
        friendRepository.save(friend);

        return friend.getId();
    }

    @Transactional
    public Long denyFriendRequest(Long userId, Long targetId) {
        domainService.checkExistUser(targetId);
        checkHasFriendRequestOfOneWay(targetId, userId);

        Friend friend = findFriend(targetId, userId, WAIT);
        friendRepository.delete(friend);

        return friend.getId();
    }

    @Transactional
    public Long deleteFriend(Long userId, Long targetId) {
        domainService.checkExistUser(targetId);

        Friend friend1 = findFriend(userId, targetId, ACCEPT);
        friendRepository.delete(friend1);

        Friend friend2 = findFriend(targetId, userId, ACCEPT);
        friendRepository.delete(friend2);

        return friend1.getId();
    }


    public DataListResDto<DefaultUserResDto> getFriendRequestList(Long userId) {
        List<DefaultUserResDto> requestReceivers = friendRepository.getFriendRequestList(userId);

        return new DataListResDto<>(0, requestReceivers);
    }

    public DataListResDto<DefaultUserResDto> getFriendWaitList(Long userId) {
        List<DefaultUserResDto> requestSenders = friendRepository.getFriendWaitList(userId);

        return new DataListResDto<>(0, requestSenders);
    }

    public DataListResDto<ActiveUserResDto> getFriendList(Long userId, FriendFilterDto friendFilter) {
        List<ActiveUserResDto> friendList = friendRepository.getFriendList(userId, friendFilter);

        return new DataListResDto<>(0, friendList);
    }

    private Friend findFriend(Long senderId, Long receiverId, FriendStatus acceptStatus){
        return friendRepository.findBySenderIdAndReceiverIdAndAcceptStatus(senderId, receiverId, acceptStatus)
                .orElseThrow(() -> new FriendException(NO_SUCH_FRIEND));
    }

    private void checkAlreadyFriendRequestOfTwoWay(Long userId, Long targetId){
        if(friendRepository.existsFriendRequest(userId, targetId)){
            throw new FriendException(ALREADY_FRIEND_REQUEST);
        }
    }

    private void checkHasFriendRequestOfOneWay(Long senderId, Long receiverId){
        if(!friendRepository.existsBySenderIdAndReceiverIdAndAcceptStatus(senderId, receiverId, WAIT)){
            throw new FriendException(NO_SUCH_FRIEND_REQUEST);
        }
    }
}
