package houseInception.connet.contoller;

import houseInception.connet.dto.*;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.BaseResultDto;
import houseInception.connet.service.GptRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/gptRooms")
@RequiredArgsConstructor
@RestController
public class GptRoomController {

    private final GptRoomService gptRoomService;

    @PostMapping
    public BaseResponse<GptChatResDto> addGptChat(@RequestBody @Valid GptRoomChatAddDto gptRoomChatAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        GptChatResDto result = gptRoomService.addGptChat(userId, gptRoomChatAddDto);

        return new BaseResponse<>(result);
    }

    @GetMapping
    public BaseResponse<DataListResDto<GptRoomListResDto>> getGptRoomList(@RequestParam(defaultValue = "1") int page){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<GptRoomListResDto> result = gptRoomService.getGptChatRoomList(userId, page);

        return new BaseResponse<>(result);
    }

    @PatchMapping("/{gptRoomUuid}")
    public BaseResponse updateGptRoom(@PathVariable String gptRoomUuid,
                                      @RequestBody @Valid GptRoomUpdateDto gptRoomUpdateDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = gptRoomService.updateGptRoom(userId, gptRoomUuid, gptRoomUpdateDto.getTitle());

        return BaseResponse.getSimpleRes(resultId);
    }

    @DeleteMapping("/{gptRoomUuid}")
    public BaseResponse<BaseResultDto> deleteGptRoom(@PathVariable String gptRoomUuid){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = gptRoomService.deleteGptRoom(userId, gptRoomUuid);

        return BaseResponse.getSimpleRes(resultId);
    }

    @GetMapping("/{gptRoomUuid}")
    public BaseResponse<GptChatRoomChatListResDto> getGptRoomChatList(@PathVariable String gptRoomUuid,
                                                                      @RequestParam(defaultValue = "1") int page){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        GptChatRoomChatListResDto result = gptRoomService.getGptChatRoomChatList(userId, gptRoomUuid, page);

        return new BaseResponse<>(result);
    }
}
