package houseInception.connet.contoller;

import houseInception.connet.dto.DataListResDto;
import houseInception.connet.dto.group.*;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.DefaultIdDto;
import houseInception.connet.response.DefaultUuidDto;
import houseInception.connet.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/groups")
@RequiredArgsConstructor
@RestController
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public BaseResponse<DefaultUuidDto> addGroup(@ModelAttribute @Valid GroupAddDto groupAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        String resultUuid = groupService.addGroup(userId, groupAddDto);

        return BaseResponse.getSimpleRes(resultUuid);
    }

    @PatchMapping("/{groupUuid}")
    public BaseResponse<DefaultUuidDto> updateGroup(@PathVariable String groupUuid,
                                                    @ModelAttribute @Valid GroupUpdateDto updateDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        String resultUuid = groupService.updateGroup(userId, groupUuid, updateDto);

        return BaseResponse.getSimpleRes(resultUuid);
    }

    @GetMapping
    public DataListResDto<GroupResDto> getGroupList(@RequestParam(defaultValue = "1") int page){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<GroupResDto> result = groupService.getGroupList(userId, page);

        return result;
    }

    @GetMapping("/{groupUuid}/groupUsers")
    public BaseResponse<List<GroupUserResDto>> getGroupUserList(@PathVariable String groupUuid){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        List<GroupUserResDto> result = groupService.getGroupUserList(userId, groupUuid);

        return new BaseResponse<>(result);
    }

    @PostMapping("/{groupUuid}/enter")
    public BaseResponse<DefaultUuidDto> enterGroup(@PathVariable String groupUuid){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        String resultUuid = groupService.enterGroup(userId, groupUuid);

        return BaseResponse.getSimpleRes(resultUuid);
    }

    @DeleteMapping("/{groupUuid}/exit")
    public BaseResponse<DefaultIdDto> exitGroup(@PathVariable String groupUuid){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = groupService.exitGroup(userId, groupUuid);

        return BaseResponse.getSimpleRes(resultId);
    }

    @GetMapping("/public")
    public BaseResponse<DataListResDto<PublicGroupResDto>> getPublicGroupList(@ModelAttribute GroupFilter filter){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<PublicGroupResDto> result = groupService.getPublicGroupList(userId, filter);

        return new BaseResponse<>(result);
    }

    @GetMapping("/{groupUuid}")
    public BaseResponse<GroupDetailResDto> getGroupDetail(@PathVariable String groupUuid){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        GroupDetailResDto result = groupService.getGroupDetail(userId, groupUuid);

        return new BaseResponse<>(result);
    }
}
