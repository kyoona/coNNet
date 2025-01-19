package houseInception.connet.dto.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TapResDto {

    private Long tapId;
    private String tapName;
    private boolean isUnread;
}
