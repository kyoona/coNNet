package houseInception.connet.contoller;

import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.BaseResultDto;
import houseInception.connet.service.UserBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
