package houseInception.gptComm.service;

import houseInception.gptComm.domain.User;
import houseInception.gptComm.dto.LoginResDto;
import houseInception.gptComm.dto.SignInDto;
import houseInception.gptComm.exception.InValidTokenException;
import houseInception.gptComm.exception.UserException;
import houseInception.gptComm.externalServiceProvider.google.GoogleOathProvider;
import houseInception.gptComm.externalServiceProvider.google.GoogleUserInfo;
import houseInception.gptComm.jwt.JwtTokenProvider;
import houseInception.gptComm.repository.UserRepository;
import houseInception.gptComm.response.status.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.gptComm.domain.Status.ALIVE;
import static houseInception.gptComm.response.status.BaseErrorCode.INVALID_GOOGLE_TOKEN;
import static houseInception.gptComm.response.status.BaseErrorCode.NO_SUCH_USER;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LoginService {

    private final UserRepository userRepository;
    private final GoogleOathProvider googleOathProvider;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public LoginResDto signIn(SignInDto signInDto) {
        GoogleUserInfo userInfo = googleOathProvider.getUserInfo(signInDto.getGoogleToken());
        if (userInfo == null){
            throw new InValidTokenException(INVALID_GOOGLE_TOKEN);
        }

        String accessToken = tokenProvider.createAccessToken(userInfo.getEmail());
        String refreshToken = tokenProvider.createRefreshToken(userInfo.getEmail());

        if (isNotServiceUser(userInfo.getEmail())) {
            User user = User.create(userInfo.getName(), userInfo.getPicture(), userInfo.getEmail(), refreshToken);
            userRepository.save(user);
        } else {
            User user = findUser(userInfo.getEmail());
            user.setRefreshToken(refreshToken);
        }

        return new LoginResDto(accessToken, refreshToken);
    }

    private boolean isNotServiceUser(String email){
        return !userRepository.existsByEmailAndStatus(email, ALIVE);
    }

    private User findUser(String email){
        User user = userRepository.findByEmailAndStatus(email, ALIVE).orElse(null);
        if (user == null) {
            throw new UserException(NO_SUCH_USER);
        }

        return user;
    }
}
