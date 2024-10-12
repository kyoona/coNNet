package houseInception.gptComm.contoller;

import houseInception.gptComm.dto.ChatAddDto;
import houseInception.gptComm.dto.DataListResDto;
import houseInception.gptComm.dto.GptChatResDto;
import houseInception.gptComm.dto.GptChatRoomListResDto;
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
    public GptChatResDto addGptChat(@RequestBody @Valid ChatAddDto chatAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        GptChatResDto result = chatRoomService.addGptChat(userId, chatAddDto);

        return result;
    }

    @GetMapping("/gpt")
    public DataListResDto<GptChatRoomListResDto> getGptChatRoomList(@RequestParam(defaultValue = "1") int page){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        DataListResDto<GptChatRoomListResDto> result = chatRoomService.getGptChatRoomList(userId, page);

        return result;
    }
}
