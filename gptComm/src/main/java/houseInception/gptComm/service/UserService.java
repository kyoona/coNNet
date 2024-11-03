package houseInception.gptComm.service;

import houseInception.gptComm.domain.User;
import houseInception.gptComm.dto.UserResDto;
import houseInception.gptComm.exception.UserException;
import houseInception.gptComm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.gptComm.domain.Status.ALIVE;
import static houseInception.gptComm.domain.Status.DELETED;
import static houseInception.gptComm.response.status.BaseErrorCode.NO_SUCH_USER;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResDto getUserInfo(Long userId, String email) {
        if (email == null) {
            return new UserResDto(findUser(userId));
        }else {
            return userRepository.findUserByEmailWithFriendRelation(userId, email);
        }
    }

    private User findUserByEmail(String email){
        User user = userRepository.findByEmailAndStatus(email, ALIVE).orElse(null);
        if (user == null) {
            throw new UserException(NO_SUCH_USER);
        }

        return user;
    }

    private User findUser(Long userId){
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getStatus() == DELETED) {
            throw new UserException(NO_SUCH_USER);
        }

        return user;
    }
}
