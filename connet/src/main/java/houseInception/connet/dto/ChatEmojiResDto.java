package houseInception.connet.dto;

import houseInception.connet.domain.EmojiType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatEmojiResDto {

    private EmojiType emojiType;
    private int count;
}
