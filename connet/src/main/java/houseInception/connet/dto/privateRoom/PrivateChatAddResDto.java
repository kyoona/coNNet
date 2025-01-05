package houseInception.connet.dto.privateRoom;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PrivateChatAddResDto {

    private String chatRoomUuid;
    private Long chatId;
    private LocalDateTime createAt;
}
