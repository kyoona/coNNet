package houseInception.connet.service.util;

import houseInception.connet.domain.User;
import houseInception.connet.exception.GroupException;
import houseInception.connet.exception.UserBlockException;
import houseInception.connet.exception.UserException;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.repository.UserBlockRepository;
import houseInception.connet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.response.status.BaseErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DomainValidatorUtil {

    private final UserRepository userRepository;
    private final UserBlockRepository userBlockRepository;
    private final GroupRepository groupRepository;

    public User findUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NO_SUCH_USER));
    }

    public void checkExistUser(Long userId){
        if(!userRepository.existsByIdAndStatus(userId, ALIVE)){
            throw new UserException(NO_SUCH_USER);
        }
    }

    public void checkNotUserBlock(Long userId, Long targetId){
        if (userBlockRepository.existsByUserIdAndTargetId(userId, targetId)){
            throw new UserBlockException(BLOCK_USER);
        }
    }

    public void checkExistGroup(String groupUuid){
        if(!groupRepository.existsByGroupUuidAndStatus(groupUuid, ALIVE)){
            throw new GroupException(NO_SUCH_GROUP);
        }
    }
}
