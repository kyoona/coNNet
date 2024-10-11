package houseInception.gptComm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GptChatResDto {

    private String chatRoomUuid;
    private String title;
    private Long userChatId;
    private Long gptChatId;
    private String message;
}
