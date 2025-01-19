package houseInception.connet.contoller;

import houseInception.connet.dto.DataListResDto;
import houseInception.connet.dto.groupChat.*;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.service.GroupChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/groups/{groupUuid}/chats")
@RequiredArgsConstructor
@RestController
public class GroupChatController {

    private final GroupChatService groupChatService;

    @PostMapping
    public BaseResponse<GroupChatAddResDto> addChat(@PathVariable String groupUuid,
                                                    @ModelAttribute @Valid GroupChatAddDto chatAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        GroupChatAddResDto result = groupChatService.addChat(userId, groupUuid, chatAddDto);

        return new BaseResponse<>(result);
    }

    @PostMapping("/gpt")
    public BaseResponse<GroupGptChatAddResDto> addGptChat(@PathVariable String groupUuid,
                                                          @RequestBody @Valid GroupGptChatAddDto chatAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        GroupGptChatAddResDto result = groupChatService.addGptChat(userId, groupUuid, chatAddDto);

        return new BaseResponse<>(result);
    }

    @GetMapping
    public BaseResponse<DataListResDto<GroupChatResDto>> getChatList(@PathVariable String groupUuid,
                                                                     @RequestParam(required = true) Long tapId,
                                                                     @RequestParam(defaultValue = "1") int page){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<GroupChatResDto> result = groupChatService.getChatList(userId, groupUuid, tapId, page);

        return new BaseResponse<>(result);
    }
}
