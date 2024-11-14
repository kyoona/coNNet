package houseInception.connet.contoller;

import houseInception.connet.dto.ActiveUserResDto;
import houseInception.connet.dto.DataListResDto;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.BaseResultDto;
import houseInception.connet.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/friend")
@RequiredArgsConstructor
@RestController
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/{targetId}")
    public BaseResponse<BaseResultDto> requestFriend(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.requestFriend(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @PostMapping("/{targetId}/accept")
    public BaseResponse<BaseResultDto> acceptFriendRequest(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.acceptFriendRequest(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @PostMapping("/{targetId}/deny")
    public BaseResponse<BaseResultDto> denyFriendRequest(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.denyFriendRequest(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @DeleteMapping("/{targetId}")
    public BaseResponse<BaseResultDto> deleteFriend(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.deleteFriend(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @GetMapping("/wait")
    public BaseResponse<DataListResDto<DefaultUserResDto>> getFriendWaitList(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<DefaultUserResDto> result = friendService.getFriendWaitList(userId);

        return new BaseResponse<>(result);
    }

    @GetMapping
    public BaseResponse<DataListResDto<ActiveUserResDto>> getFriendList(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<ActiveUserResDto> result = friendService.getFriendList(userId);

        return new BaseResponse<>(result);
    }
}
