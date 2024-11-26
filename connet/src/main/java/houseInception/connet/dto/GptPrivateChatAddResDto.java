package houseInception.connet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GptPrivateChatAddResDto {

    private Long userChatId;
    private LocalDateTime userChatCreateAt;
    private Long gptChatId;
    private LocalDateTime gptChatCreateAt;
    private String message;
}
