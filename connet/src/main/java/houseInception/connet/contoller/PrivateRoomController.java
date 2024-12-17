package houseInception.connet.contoller;

import houseInception.connet.dto.*;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.BaseResultDto;
import houseInception.connet.service.PrivateRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/privateRooms")
@RequiredArgsConstructor
@RestController
public class PrivateRoomController {

    private final PrivateRoomService privateRoomService;

    @PostMapping("/{targetId}")
    public BaseResponse<PrivateChatAddResDto> addPrivateChat(@PathVariable Long targetId,
                                                             @ModelAttribute PrivateChatAddDto chatAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        PrivateChatAddResDto result = privateRoomService.addPrivateChat(userId, targetId, chatAddDto);

        return new BaseResponse<>(result);
    }

    @PostMapping("/{targetId}/gpt")
    public BaseResponse<GptPrivateChatAddResDto> addGptChat(@PathVariable Long targetId,
                                                            @RequestBody PrivateGptChatAddDto gptChatAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        GptPrivateChatAddResDto result = privateRoomService.addGptChat(userId, targetId, gptChatAddDto.getMessage());

        return new BaseResponse<>(result);
    }

    @DeleteMapping("/{privateRoomUuid}")
    public BaseResponse<BaseResultDto> deletePrivateRoom(@PathVariable String privateRoomUuid){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = privateRoomService.deletePrivateRoom(userId, privateRoomUuid);

        return BaseResponse.getSimpleRes(resultId);
    }

    @GetMapping
    public BaseResponse<DataListResDto<PrivateRoomResDto>> getPrivateRoomList(@RequestParam(defaultValue = "1") int page){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<PrivateRoomResDto> result = privateRoomService.getPrivateRoomList(userId, page);

        return new BaseResponse<>(result);
    }

    @GetMapping("/{privateRoomUuid}/chats")
    public BaseResponse<DataListResDto<PrivateChatResDto>> getPrivateChatList(@PathVariable String privateRoomUuid,
                                                                              @RequestParam(defaultValue = "1") int page){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<PrivateChatResDto> result = privateRoomService.getPrivateChatList(userId, privateRoomUuid, page);

        return new BaseResponse<>(result);
    }

}
