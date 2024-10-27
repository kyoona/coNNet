package houseInception.gptComm.contoller;

import houseInception.gptComm.dto.DataListResDto;
import houseInception.gptComm.dto.UserResDto;
import houseInception.gptComm.response.BaseResponse;
import houseInception.gptComm.response.BaseResultDto;
import houseInception.gptComm.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{userId}/accept")
    public BaseResponse<BaseResultDto> acceptFriendRequest(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.acceptFriendRequest(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @PostMapping("/{userId}/deny")
    public BaseResponse<BaseResultDto> denyFriendRequest(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.denyFriendRequest(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @GetMapping("/wait")
    public BaseResponse<DataListResDto<UserResDto>> getFriendWaitList(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<UserResDto> result = friendService.getFriendWaitList(userId);

        return new BaseResponse<>(result);
    }

    @GetMapping
    public BaseResponse<DataListResDto<UserResDto>> getFriendList(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<UserResDto> result = friendService.getFriendList(userId);

        return new BaseResponse<>(result);
    }
}
