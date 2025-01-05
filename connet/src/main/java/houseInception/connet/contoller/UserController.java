package houseInception.connet.contoller;

import houseInception.connet.domain.user.Setting;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.user.CommonGroupOfUserResDto;
import houseInception.connet.dto.user.SettingUpdateDto;
import houseInception.connet.dto.user.UserProfileResDto;
import houseInception.connet.dto.user.UserProfileUpdateDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.DefaultIdDto;
import houseInception.connet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping
    public BaseResponse<UserProfileResDto> getSelfProfile(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        UserProfileResDto result = userService.getSelfProfile(userId);

        return new BaseResponse<>(result);
    }

    @GetMapping("/{userId}")
    public BaseResponse<UserProfileResDto> getUserProfile(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        UserProfileResDto result = userService.getUserProfile(userId);

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

    @GetMapping("/setting")
    public BaseResponse<Setting> getSetting(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Setting result = userService.getSetting(userId);

        return new BaseResponse<>(result);
    }

    @GetMapping("/{targetId}/groups")
    public BaseResponse<List<CommonGroupOfUserResDto>> getCommonGroupList(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        List<CommonGroupOfUserResDto> result = userService.getCommonGroupList(userId, targetId);

        return new BaseResponse<>(result);
    }
}
