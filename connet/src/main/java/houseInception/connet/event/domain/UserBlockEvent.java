package houseInception.connet.event.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserBlockEvent {

    private Long userId;
    private Long targetId;
}
