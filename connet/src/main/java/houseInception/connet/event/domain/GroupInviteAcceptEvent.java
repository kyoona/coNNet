package houseInception.connet.event.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupInviteAcceptEvent {

    private Long userId;
    private String groupUuid;
}
