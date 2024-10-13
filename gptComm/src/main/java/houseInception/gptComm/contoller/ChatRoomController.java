package houseInception.gptComm.contoller;

import houseInception.gptComm.dto.*;
import houseInception.gptComm.response.BaseResponse;
import houseInception.gptComm.response.BaseResultDto;
import houseInception.gptComm.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/chatRooms")
@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/gpt")
    public BaseResponse<GptChatResDto> addGptChat(@RequestBody @Valid ChatAddDto chatAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        GptChatResDto result = chatRoomService.addGptChat(userId, chatAddDto);

        return new BaseResponse<>(result);
    }

    @GetMapping("/gpt")
    public BaseResponse<DataListResDto<GptChatRoomListResDto>> getGptChatRoomList(@RequestParam(defaultValue = "1") int page){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<GptChatRoomListResDto> result = chatRoomService.getGptChatRoomList(userId, page);

        return new BaseResponse<>(result);
    }

    @DeleteMapping("/{chatRoomUuid}")
    public BaseResponse<BaseResultDto> deleteChatRoom(@PathVariable String chatRoomUuid){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = chatRoomService.deleteChatRoom(userId, chatRoomUuid);

        return BaseResponse.getSimpleRes(resultId);
    }

    @GetMapping("/gpt/{chatRoomUuid}")
    public BaseResponse<GptChatRoomChatListResDto> getGptChatRoomChatList(@PathVariable String chatRoomUuid,
                                                            @RequestParam(defaultValue = "1") int page){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        GptChatRoomChatListResDto result = chatRoomService.getGptChatRoomChatList(userId, chatRoomUuid, page);

        return new BaseResponse<>(result);
    }
}
