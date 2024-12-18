package houseInception.connet.dto.chatEmoji;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ChatEmojiUserResDto {

    private String userName;

    @QueryProjection
    public ChatEmojiUserResDto(String userName) {
        this.userName = userName;
    }
}
