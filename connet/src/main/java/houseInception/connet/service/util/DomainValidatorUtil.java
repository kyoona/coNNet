package houseInception.connet.service.util;

import houseInception.connet.domain.User;
import houseInception.connet.exception.UserException;
import houseInception.connet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.response.status.BaseErrorCode.NO_SUCH_USER;

@RequiredArgsConstructor
@Transactional
@Service
public class DomainValidatorUtil {

    private final UserRepository userRepository;

    public User findUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NO_SUCH_USER));
    }

    public void checkExistUser(Long userId){
        if(!userRepository.existsByIdAndStatus(userId, ALIVE)){
            throw new UserException(NO_SUCH_USER);
        }
    }
}
