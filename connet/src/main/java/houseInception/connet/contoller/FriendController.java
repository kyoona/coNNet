package houseInception.connet.contoller;

import houseInception.connet.dto.*;
import houseInception.connet.dto.friend.FriendFilterDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.DefaultIdDto;
import houseInception.connet.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/friends")
@RequiredArgsConstructor
@RestController
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/{targetId}/request")
    public BaseResponse<DefaultIdDto> requestFriend(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.requestFriendById(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @PostMapping("/request")
    public BaseResponse<DefaultIdDto> requestFriend(@RequestBody @Valid EmailDto emailDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.requestFriendByEmail(userId, emailDto.getEmail());

        return BaseResponse.getSimpleRes(resultId);
    }

    @GetMapping("/request")
    public BaseResponse<List<DefaultUserResDto>> getFriendRequestList(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        List<DefaultUserResDto> result = friendService.getFriendRequestList(userId);

        return new BaseResponse<>(result);
    }

    @DeleteMapping("/{targetId}/request")
    public BaseResponse<DefaultIdDto> cancelFriendRequest(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.cancelFriendRequest(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @PostMapping("/{targetId}/accept")
    public BaseResponse<DefaultIdDto> acceptFriendRequest(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.acceptFriendRequest(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @PostMapping("/{targetId}/deny")
    public BaseResponse<DefaultIdDto> denyFriendRequest(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.denyFriendRequest(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @DeleteMapping("/{targetId}")
    public BaseResponse<DefaultIdDto> deleteFriend(@PathVariable Long targetId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = friendService.deleteFriend(userId, targetId);

        return BaseResponse.getSimpleRes(resultId);
    }

    @GetMapping("/wait")
    public BaseResponse<List<DefaultUserResDto>> getFriendWaitList(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        List<DefaultUserResDto> result = friendService.getFriendWaitList(userId);

        return new BaseResponse<>(result);
    }

    @GetMapping
    public BaseResponse<List<ActiveUserResDto>> getFriendList(@ModelAttribute FriendFilterDto friendFilter){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        List<ActiveUserResDto> result = friendService.getFriendList(userId, friendFilter);

        return new BaseResponse<>(result);
    }
}
