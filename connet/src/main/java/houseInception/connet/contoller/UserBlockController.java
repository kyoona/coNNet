package houseInception.connet.contoller;

import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.DefaultIdDto;
import houseInception.connet.service.UserBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/userBlocks")
@RequiredArgsConstructor
@RestController
public class UserBlockController {

    private final UserBlockService userBlockService;

    @PostMapping("/{targetId}")
    public BaseResponse<DefaultIdDto> blockUser(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = userBlockService.blockUser(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @DeleteMapping("/{targetId}")
    public BaseResponse<DefaultIdDto> cancelBlock(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = userBlockService.cancelBlock(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @GetMapping
    public BaseResponse<List<DefaultUserResDto>> getBlockUserList(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        List<DefaultUserResDto> result = userBlockService.getBlockUserList(userId);

        return new BaseResponse<>(result);
    }
}
