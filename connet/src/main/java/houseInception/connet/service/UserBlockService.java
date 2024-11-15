package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.domain.UserBlock;
import houseInception.connet.domain.UserBlockType;
import houseInception.connet.dto.DataListResDto;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.event.publisher.UserBlockEventPublisher;
import houseInception.connet.exception.UserBlockException;
import houseInception.connet.exception.UserException;
import houseInception.connet.repository.UserBlockRepository;
import houseInception.connet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.Status.DELETED;
import static houseInception.connet.domain.UserBlockType.ACCEPT;
import static houseInception.connet.domain.UserBlockType.REQUEST;
import static houseInception.connet.response.status.BaseErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserBlockService {

    private final UserBlockRepository userBlockRepository;
    private final UserRepository userRepository;

    private final UserBlockEventPublisher userBlockEventPublisher;

    @Transactional
    public Long blockUser(Long userId, Long targetId) {
        //이미 차단된 유저인지 확인해야함
        User targetUser = findUser(targetId);
        User user = findUser(userId);

        UserBlock findUserBlock = userBlockRepository.findByUserIdAndTargetId(userId, targetId).orElse(null);
        checkAlreadyRequestBlock(findUserBlock);

        userBlockEventPublisher.publishUserBlockEvent(user, targetUser);

        if(findUserBlock == null){
            UserBlock userBlock1 = UserBlock.create(user, targetUser, REQUEST);
            userBlockRepository.save(userBlock1);

            UserBlock userBlock2 = UserBlock.create(targetUser, user, ACCEPT);
            userBlockRepository.save(userBlock2);

            return userBlock1.getId();
        }else{
            findUserBlock.setBlockType(REQUEST);

            return findUserBlock.getId();
        }
    }

    @Transactional
    public Long cancelBlock(Long userId, Long targetId) {
        checkUser(targetId);
        UserBlock userBlock = findUserBlock(userId, targetId);
        UserBlock reverseUserBlock = findUserBlock(targetId, userId);

        userBlockRepository.delete(userBlock);
        if(reverseUserBlock.getBlockType() == ACCEPT){
            userBlockRepository.delete(reverseUserBlock);
        }

        return userBlock.getId();
    }

    public DataListResDto<DefaultUserResDto> getBlockUserList(Long userId) {
        List<DefaultUserResDto> blockUserList = userBlockRepository.getBlockUserList(userId);

        return new DataListResDto<DefaultUserResDto>(0, blockUserList);
    }

    private User findUser(Long userId){
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getStatus() == DELETED) {
            throw new UserException(NO_SUCH_USER);
        }

        return user;
    }

    private UserBlock findUserBlock(Long userId, Long targetId){
        UserBlock userBlock = userBlockRepository.findByUserIdAndTargetId(userId, targetId).orElse(null);
        if (userBlock == null) {
            throw new UserBlockException(NO_SUCH_USER_BLOCK);
        }

        return userBlock;
    }

    private void checkUser(Long userId){
        if(!userRepository.existsByIdAndStatus(userId, ALIVE)){
            throw new UserException(NO_SUCH_USER);
        }
    }

    private void checkAlreadyRequestBlock(UserBlock findUserBlock) {
        if(findUserBlock != null && findUserBlock.getBlockType() == REQUEST){
            throw new UserBlockException(ALREADY_BLOCK_USER);
        }
    }
}
