package houseInception.gptComm.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GptRoomListResDto {

    private String chatRoomUuid;
    private String title;
    private LocalDateTime createAt;

    @QueryProjection
    public GptRoomListResDto(String gptRoomUuid, String title, LocalDateTime createAt) {
        this.chatRoomUuid = gptRoomUuid;
        this.title = title;
        this.createAt = createAt;
    }
}
