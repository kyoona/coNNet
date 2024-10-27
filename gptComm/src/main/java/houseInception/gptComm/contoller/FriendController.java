package houseInception.gptComm.contoller;

import houseInception.gptComm.response.BaseResponse;
import houseInception.gptComm.response.BaseResultDto;
import houseInception.gptComm.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/friend")
@RequiredArgsConstructor
@RestController
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/{userId}")
    public BaseResponse<BaseResultDto> requestFriend(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.requestFriend(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }
}
