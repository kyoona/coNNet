package houseInception.connet.dto.chatEmoji;

import houseInception.connet.domain.EmojiType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmojiDto {

    @NotNull
    private EmojiType emojiType;
}
