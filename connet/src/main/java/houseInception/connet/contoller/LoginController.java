package houseInception.connet.contoller;

import houseInception.connet.dto.login.AccessTokenDto;
import houseInception.connet.dto.login.TokenResDto;
import houseInception.connet.dto.login.RefreshDto;
import houseInception.connet.dto.login.SignInDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.DefaultIdDto;
import houseInception.connet.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequestMapping("/login")
@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/sign-in")
    public BaseResponse<TokenResDto> signIn(@RequestBody @Valid SignInDto signInDto){
        TokenResDto result = loginService.signIn(signInDto);

        return new BaseResponse<>(result);
    }

    @PostMapping("/refresh")
    public BaseResponse<TokenResDto> refresh(@RequestBody @Valid RefreshDto refreshDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        TokenResDto result = loginService.refresh(userId, refreshDto.getRefreshToken());

        return new BaseResponse<>(result);
    }

    @PostMapping("/refresh/check")
    public BaseResponse<Map<String, Boolean>> checkRefreshToken(@RequestBody @Valid RefreshDto refreshDto){
        boolean isValid = loginService.checkRefreshToken(refreshDto.getRefreshToken());

        return new BaseResponse<>(Map.of("isValid", isValid));
    }

    @PostMapping("/access/check")
    public BaseResponse<Map<String, Boolean>> checkAccessToken(@RequestBody @Valid AccessTokenDto accessTokenDto){
        boolean isValid = loginService.checkAccessToken(accessTokenDto.getAccessToken());

        return new BaseResponse<>(Map.of("isValid", isValid));
    }

    @PostMapping("/sign-out")
    public BaseResponse<DefaultIdDto> signOut(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = loginService.signOut(userId);

        return BaseResponse.getSimpleRes(resultId);
    }
}
