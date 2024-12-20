package houseInception.connet.contoller;

import houseInception.connet.dto.group_invite.GroupInviteDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.DefaultIdDto;
import houseInception.connet.service.GroupInviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/groups/{groupUuid}/invite")
@RequiredArgsConstructor
@RestController
public class GroupInviteController {

    private final GroupInviteService groupInviteService;

    @GetMapping
    public BaseResponse<DefaultIdDto> inviteGroup(@PathVariable String groupUuid,
                                                  @RequestBody @Valid GroupInviteDto inviteDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = groupInviteService.inviteGroup(userId, groupUuid, inviteDto);

        return BaseResponse.getSimpleRes(resultId);
    }
}
