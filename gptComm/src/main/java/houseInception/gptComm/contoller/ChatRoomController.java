package houseInception.gptComm.contoller;

import houseInception.gptComm.dto.ChatAddDto;
import houseInception.gptComm.dto.GptChatResDto;
import houseInception.gptComm.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
