package houseInception.connet.contoller;

import houseInception.connet.domain.EmojiType;
import houseInception.connet.dto.chatEmoji.EmojiDto;
import houseInception.connet.dto.chatEmoji.ChatEmojiUserResDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.DefaultIdDto;
import houseInception.connet.service.ChatEmojiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatEmojiController {

    private ChatEmojiService chatEmojiService;

    @PostMapping("/privateChats/{chatId}/emojis")
    public BaseResponse<DefaultIdDto> addEmojiOfPrivate(@PathVariable Long chatId,
                                                        @RequestBody EmojiDto emojiDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = chatEmojiService.addEmojiToPrivateChat(userId, chatId, emojiDto);

        return BaseResponse.getSimpleRes(resultId);
    }

    @DeleteMapping("/privateChats/{chatId}/emojis")
    public BaseResponse<DefaultIdDto> removeEmojiOfPrivate(@PathVariable Long chatId,
                                                           @RequestBody EmojiDto emojiDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = chatEmojiService.removeEmojiToPrivateChat(userId, chatId, emojiDto);

        return BaseResponse.getSimpleRes(resultId);
    }

    @GetMapping("/privateChats/{chatId}/emojis")
    public BaseResponse<List<ChatEmojiUserResDto>> getEmojiInfo(@PathVariable Long chatId,
                                                                @RequestParam EmojiType emojiType){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        List<ChatEmojiUserResDto> result = chatEmojiService.getEmojiInfoInPrivateRoom(userId, chatId, emojiType);

        return new BaseResponse<>(result);
    }

    @PostMapping("/groupChats/{chatId}/emojis")
    public BaseResponse<DefaultIdDto> addEmojiOfGroup(@PathVariable Long chatId,
                                                      @RequestBody EmojiDto emojiDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = chatEmojiService.addEmojiToGroupChat(userId, chatId, emojiDto);

        return BaseResponse.getSimpleRes(resultId);
    }

    @DeleteMapping("/groupChats/{chatId}/emojis")
    public BaseResponse<DefaultIdDto> removeEmojiOfGroup(@PathVariable Long chatId,
                                                         @RequestBody EmojiDto emojiDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = chatEmojiService.removeEmojiToGroupChat(userId, chatId, emojiDto);

        return BaseResponse.getSimpleRes(resultId);
    }
}
