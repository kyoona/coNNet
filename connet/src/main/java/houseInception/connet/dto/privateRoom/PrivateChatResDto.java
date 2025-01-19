package houseInception.connet.dto.privateRoom;

import com.querydsl.core.annotations.QueryProjection;
import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.EmojiType;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.chatEmoji.ChatEmojiResDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class PrivateChatResDto {

    private Long chatId;
    private String message;
    private String image;
    private ChatterRole writerRole;
    private DefaultUserResDto writer;
    private LocalDateTime createAt;
    private List<ChatEmojiResDto> emoji = new ArrayList<>();

    @QueryProjection
    public PrivateChatResDto(Long chatId, String message, String image, ChatterRole writerRole, DefaultUserResDto writer, LocalDateTime createAt, String emojiAggStr) {
        this.chatId = chatId;
        this.message = message;
        this.image = image;
        this.writerRole = writerRole;
        this.writer = writer;
        this.createAt = createAt;

        parseEmojiAggStr(emojiAggStr);
    }

    private void parseEmojiAggStr(String emojiAggStr){
        if (emojiAggStr != null && !emojiAggStr.isEmpty()) {
            String[] splitEmoji = emojiAggStr.split(",");
            Arrays.stream(splitEmoji)
                    .collect(Collectors.toMap(
                            emoji -> emoji,
                            emoji -> 1,
                            Integer::sum
                    )).forEach((key, value) -> emoji.add(new ChatEmojiResDto(EmojiType.valueOf(key), value)));
        }
    }
}
