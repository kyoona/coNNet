package houseInception.connet.contoller;

import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.user.SettingUpdateDto;
import houseInception.connet.dto.user.UserProfileUpdateDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.DefaultIdDto;
import houseInception.connet.service.UserService;
import jakarta.validation.Valid;
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

    @PatchMapping
    public BaseResponse<DefaultIdDto> updateProfile(@ModelAttribute @Valid UserProfileUpdateDto profileDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = userService.updateProfile(userId, profileDto);

        return BaseResponse.getSimpleRes(resultId);
    }

    @DeleteMapping
    public BaseResponse<DefaultIdDto> deleteUser(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = userService.deleteUser(userId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @PostMapping("/setting")
    public BaseResponse<DefaultIdDto> updateSetting(@RequestBody SettingUpdateDto settingDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = userService.updateSetting(userId, settingDto);

        return BaseResponse.getSimpleRes(resultId);
    }
}
