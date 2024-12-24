package houseInception.connet.contoller;

import houseInception.connet.dto.groupChat.GroupChatAddDto;
import houseInception.connet.dto.groupChat.GroupChatAddResDto;
import houseInception.connet.dto.groupChat.GroupGptChatAddDto;
import houseInception.connet.dto.groupChat.GroupGptChatAddResDto;
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
}
