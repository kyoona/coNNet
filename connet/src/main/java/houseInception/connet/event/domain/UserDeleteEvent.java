package houseInception.connet.event.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDeleteEvent {

    private Long userId;
}
