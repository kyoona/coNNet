package houseInception.gptComm.service;

import houseInception.gptComm.domain.User;
import houseInception.gptComm.dto.TokenResDto;
import houseInception.gptComm.dto.SignInDto;
import houseInception.gptComm.exception.InValidTokenException;
import houseInception.gptComm.exception.UserException;
import houseInception.gptComm.externalServiceProvider.google.GoogleOathProvider;
import houseInception.gptComm.externalServiceProvider.google.GoogleUserInfo;
import houseInception.gptComm.jwt.JwtTokenProvider;
import houseInception.gptComm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.gptComm.domain.Status.ALIVE;
import static houseInception.gptComm.response.status.BaseErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LoginService {

    private final UserRepository userRepository;
    private final GoogleOathProvider googleOathProvider;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public TokenResDto signIn(SignInDto signInDto) {
        GoogleUserInfo userInfo = googleOathProvider.getUserInfo(signInDto.getGoogleToken());
        if (userInfo == null){
            throw new InValidTokenException(INVALID_GOOGLE_TOKEN);
        }

//        GoogleUserInfo userInfo = new GoogleUserInfo("a", "user name", "name", "family", "email", "dd");
        String accessToken = tokenProvider.createAccessToken(userInfo.getEmail());
        String refreshToken = tokenProvider.createRefreshToken(userInfo.getEmail());

        if (isNotServiceUser(userInfo.getEmail())) {
            User user = User.create(userInfo.getName(), userInfo.getPicture(), userInfo.getEmail(), refreshToken);
            userRepository.save(user);
        } else {
            User user = findUser(userInfo.getEmail());
            user.setRefreshToken(refreshToken);
        }

        return new TokenResDto(accessToken, refreshToken);
    }

    @Transactional
    public TokenResDto refresh(Long userId, String refreshToken) {
        User user = findUser(userId);
        if(!user.equals(refreshToken)){
            throw new InValidTokenException(INVALID_REFRESH_TOKEN);
        }
        checkValidToken(refreshToken);

        String accessToken = tokenProvider.createAccessToken(user.getEmail());
        String newRefreshToken = tokenProvider.createRefreshToken(user.getEmail());
        user.setRefreshToken(newRefreshToken);

        return new TokenResDto(accessToken, refreshToken);
    }

    public void checkRefreshToken(String refreshToken) {
        checkValidToken(refreshToken);
        if(!userRepository.existsByRefreshTokenAndStatus(refreshToken, ALIVE)){
            throw new InValidTokenException(INVALID_REFRESH_TOKEN);
        }
    }

    @Transactional
    public Long signOut(Long userId) {
        User user = findUser(userId);
        user.deleteRefreshToken();

        return user.getId();
    }

    private boolean isNotServiceUser(String email){

        return !userRepository.existsByEmailAndStatus(email, ALIVE);
    }

    private User findUser(Long userId){
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserException(NO_SUCH_USER);
        }

        return user;
    }

    private User findUser(String email){
        User user = userRepository.findByEmailAndStatus(email, ALIVE).orElse(null);
        if (user == null) {
            throw new UserException(NO_SUCH_USER);
        }

        return user;
    }

    private void checkValidToken(String refreshToken) {
        if(!tokenProvider.validateToken(refreshToken)){
            throw new InValidTokenException(INVALID_REFRESH_TOKEN);
        }
    }
}
