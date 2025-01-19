package houseInception.connet.dto.GptRoom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GptChatResDto {

    private String chatRoomUuid;
    private String title;
    private Long userChatId;
    private LocalDateTime userChatCreateAt;
    private Long gptChatId;
    private LocalDateTime gptChatCreateAt;
    private String message;
}
