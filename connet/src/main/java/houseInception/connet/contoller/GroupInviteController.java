package houseInception.connet.contoller;

import houseInception.connet.dto.group_invite.GroupInviteDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.DefaultIdDto;
import houseInception.connet.response.DefaultUuidDto;
import houseInception.connet.service.GroupInviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/groups/{groupUuid}/invite")
@RequiredArgsConstructor
@RestController
public class GroupInviteController {

    private final GroupInviteService groupInviteService;

    @PostMapping
    public BaseResponse<DefaultIdDto> inviteGroup(@PathVariable String groupUuid,
                                                  @RequestBody @Valid GroupInviteDto inviteDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = groupInviteService.inviteGroup(userId, groupUuid, inviteDto);

        return BaseResponse.getSimpleRes(resultId);
    }

    @PostMapping("/accept")
    public BaseResponse<DefaultUuidDto> acceptInvite(@PathVariable String groupUuid){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        String groupUuId = groupInviteService.acceptInvite(userId, groupUuid);

        return BaseResponse.getSimpleRes(groupUuId);
    }
}
