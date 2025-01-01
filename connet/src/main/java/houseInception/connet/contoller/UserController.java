package houseInception.connet.contoller;

import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping
    public BaseResponse<DefaultUserResDto> getSelfProfile(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DefaultUserResDto result = userService.getSelfProfile(userId);

        return new BaseResponse<>(result);
    }

    @GetMapping("/{userId}")
    public BaseResponse<DefaultUserResDto> getUserProfile(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DefaultUserResDto result = userService.getUserProfile(userId);

        return new BaseResponse<>(result);
    }
}
