package houseInception.gptComm.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GptChatRoomListResDto {

    private String chatRoomUuid;
    private String title;
    private LocalDateTime createAt;

    @QueryProjection
    public GptChatRoomListResDto(String chatRoomUuid, String title, LocalDateTime createAt) {
        this.chatRoomUuid = chatRoomUuid;
        this.title = title;
        this.createAt = createAt;
    }
}
