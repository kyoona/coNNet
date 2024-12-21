package houseInception.connet.contoller;

import houseInception.connet.dto.group.GroupAddDto;
import houseInception.connet.dto.group.GroupUserResDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.DefaultIdDto;
import houseInception.connet.response.DefaultUuidDto;
import houseInception.connet.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/groups")
@RequiredArgsConstructor
@RestController
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public BaseResponse<DefaultUuidDto> addGroup(@RequestBody GroupAddDto groupAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        String resultUuid = groupService.addGroup(userId, groupAddDto);

        return BaseResponse.getSimpleRes(resultUuid);
    }

    @GetMapping("/{groupUuid}/groupUsers")
    public BaseResponse<List<GroupUserResDto>> getGroupUserList(@PathVariable String groupUuid){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        List<GroupUserResDto> result = groupService.getGroupUserList(userId, groupUuid);

        return new BaseResponse<>(result);
    }
}
