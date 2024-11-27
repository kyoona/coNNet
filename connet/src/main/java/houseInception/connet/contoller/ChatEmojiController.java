package houseInception.connet.contoller;

import houseInception.connet.dto.EmojiDto;
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
                                                         @RequestBody EmojiDto emojiDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = chatEmojiService.addEmojiToPrivateChat(userId, chatId, emojiDto);

        return BaseResponse.getSimpleRes(resultId);
    }

    @DeleteMapping("/privateChats/{chatId}/emojis")
    public BaseResponse<BaseResultDto> removeEmojiOfPrivate(@PathVariable Long chatId,
                                                            @RequestBody EmojiDto emojiDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = chatEmojiService.removeEmojiToPrivateChat(userId, chatId, emojiDto);

        return BaseResponse.getSimpleRes(resultId);
    }
}
