package houseInception.connet.dto.groupChat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GroupChatAddResDto {

    private Long chatId;
    private LocalDateTime createAt;
}
