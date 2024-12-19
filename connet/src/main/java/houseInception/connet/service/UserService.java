package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.domain.UserBlock;
import houseInception.connet.dto.UserResDto;
import houseInception.connet.exception.UserException;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.service.util.DomainValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.Status.DELETED;
import static houseInception.connet.response.status.BaseErrorCode.NO_SUCH_USER;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final DomainValidatorUtil validator;

    public UserResDto getUserInfo(Long userId, String email) {
        if (email == null) {
            return new UserResDto(validator.findUser(userId));
        }else {
            return userRepository.findUserByEmailWithFriendRelation(userId, email);
        }
    }

    @Transactional
    public void setUserActive(Long userId){
        User user = validator.findUser(userId);
        user.setActive();
    }

    @Transactional
    public void setUserInActive(Long userId){
        User user = validator.findUser(userId);
        user.setInActive();
    }
}
