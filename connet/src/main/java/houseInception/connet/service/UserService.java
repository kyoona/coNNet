package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.service.util.DomainValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final DomainValidatorUtil validator;

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
