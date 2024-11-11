package houseInception.connet.contoller;

import houseInception.connet.dto.UserResDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping
    public BaseResponse<UserResDto> getUserInfo(@RequestParam(required = false) String email){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        UserResDto result = userService.getUserInfo(userId, email);

        return new BaseResponse<>(result);
    }
}
