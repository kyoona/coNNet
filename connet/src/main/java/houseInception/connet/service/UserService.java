package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.service.util.CommonDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CommonDomainService domainService;

    public DefaultUserResDto getSelfProfile(Long userId) {
        return userRepository.getUserProfile(userId);
    }

    @Transactional
    public void setUserActive(Long userId){
        User user = domainService.findUser(userId);
        user.setActive();
    }

    @Transactional
    public void setUserInActive(Long userId){
        User user = domainService.findUser(userId);
        user.setInActive();
    }
}
