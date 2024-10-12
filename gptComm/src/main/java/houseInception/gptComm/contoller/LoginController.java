package houseInception.gptComm.contoller;

import houseInception.gptComm.dto.TokenResDto;
import houseInception.gptComm.dto.RefreshDto;
import houseInception.gptComm.dto.SignInDto;
import houseInception.gptComm.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/login")
@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/sign-in")
    public TokenResDto signIn(@RequestBody @Valid SignInDto signInDto){
        TokenResDto result = loginService.signIn(signInDto);

        return result;
    }

    @PostMapping("/refresh")
    public TokenResDto refresh(@RequestBody @Valid RefreshDto refreshDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        TokenResDto result = loginService.refresh(userId, refreshDto.getRefreshToken());

        return result;
    }
}
