package houseInception.connet.service;

import houseInception.connet.domain.user.User;
import houseInception.connet.dto.login.TokenResDto;
import houseInception.connet.dto.login.SignInDto;
import houseInception.connet.exception.InValidTokenException;
import houseInception.connet.externalServiceProvider.google.GoogleOathProvider;
import houseInception.connet.externalServiceProvider.google.GoogleUserInfo;
import houseInception.connet.jwt.JwtTokenProvider;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.service.util.CommonDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.response.status.BaseErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LoginService {

    private final UserRepository userRepository;
    private final GoogleOathProvider googleOathProvider;
    private final JwtTokenProvider tokenProvider;
    private final CommonDomainService domainService;

    @Transactional
    public TokenResDto signIn(SignInDto signInDto) {
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
            User user = domainService.findUserByEmail(userInfo.getEmail());
            user.setRefreshToken(refreshToken);
        }

        return new TokenResDto(accessToken, refreshToken);
    }

    @Transactional
    public TokenResDto refresh(Long userId, String refreshToken) {
        User user = domainService.findUser(userId);
        if(!user.equals(refreshToken)){
            throw new InValidTokenException(INVALID_REFRESH_TOKEN);
        }
        checkValidToken(refreshToken);

        String accessToken = tokenProvider.createAccessToken(user.getEmail());
        String newRefreshToken = tokenProvider.createRefreshToken(user.getEmail());
        user.setRefreshToken(newRefreshToken);

        return new TokenResDto(accessToken, refreshToken);
    }

    public boolean checkRefreshToken(String refreshToken) {
        if (tokenProvider.validateToken(refreshToken) && userRepository.existsByRefreshTokenAndStatus(refreshToken, ALIVE)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkAccessToken(String accessToken) {
        return tokenProvider.validateToken(accessToken);
    }

    @Transactional
    public Long signOut(Long userId) {
        User user = domainService.findUser(userId);
        user.deleteRefreshToken();

        return user.getId();
    }

    private boolean isNotServiceUser(String email){
        return !userRepository.existsByEmailAndStatus(email, ALIVE);
    }

    private void checkValidToken(String refreshToken) {
        if(!tokenProvider.validateToken(refreshToken)){
            throw new InValidTokenException(INVALID_REFRESH_TOKEN);
        }
    }
}
