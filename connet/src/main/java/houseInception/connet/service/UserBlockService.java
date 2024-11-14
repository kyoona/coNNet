package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.domain.UserBlock;
import houseInception.connet.domain.UserBlockType;
import houseInception.connet.dto.DataListResDto;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.event.publisher.UserBlockEventPublisher;
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
import static houseInception.connet.response.status.BaseErrorCode.NO_SUCH_USER;

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

        UserBlock userBlock1 = UserBlock.create(user, targetUser, REQUEST);
        userBlockRepository.save(userBlock1);

        UserBlock userBlock2 = UserBlock.create(targetUser, user, ACCEPT);
        userBlockRepository.save(userBlock2);

        userBlockEventPublisher.publishUserBlockEvent(user, targetUser);

        return userBlock1.getId();
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

    private void checkUser(Long userId){
        if(!userRepository.existsByIdAndStatus(userId, ALIVE)){
            throw new UserException(NO_SUCH_USER);
        }
    }
}
