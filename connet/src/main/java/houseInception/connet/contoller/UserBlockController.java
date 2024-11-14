package houseInception.connet.contoller;

import houseInception.connet.dto.DataListResDto;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.BaseResultDto;
import houseInception.connet.service.UserBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/userBlocks")
@RequiredArgsConstructor
@RestController
public class UserBlockController {

    private final UserBlockService userBlockService;

    @PostMapping("/{targetId}")
    public BaseResponse<BaseResultDto> blockUser(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = userBlockService.blockUser(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @GetMapping
    public BaseResponse<DataListResDto<DefaultUserResDto>> getBlockUserList(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<DefaultUserResDto> result = userBlockService.getBlockUserList(userId);

        return new BaseResponse<>(result);
    }
}
