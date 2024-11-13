package houseInception.connet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GptChatRoomChatListResDto {

    private String chatRoomUuid;
    private String title;
    private int page;
    private List<GptRoomChatResDto> messages;
}
