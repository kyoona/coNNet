package houseInception.connet.contoller;

import houseInception.connet.dto.EmojiAddDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.BaseResultDto;
import houseInception.connet.service.ChatEmojiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ChatEmojiController {

    private ChatEmojiService chatEmojiService;

    @PostMapping("/privateChats/{chatId}/emojis")
    public BaseResponse<BaseResultDto> addEmojiOfPrivate(@PathVariable Long chatId,
                                                         @RequestBody EmojiAddDto emojiAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = chatEmojiService.addEmojiToPrivateChat(userId, chatId, emojiAddDto);

        return BaseResponse.getSimpleRes(resultId);
    }
}
