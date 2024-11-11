package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.dto.UserResDto;
import houseInception.connet.exception.UserException;
import houseInception.connet.repository.UserRepository;
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
