package houseInception.connet.event.domain.alarmEvent;

import lombok.Getter;

@Getter
public class FriendAlarmEvent extends AlarmEvent{

    private Long requestUserId;

    public FriendAlarmEvent(Long userId, Long requestUserId) {
        super(userId);
        this.requestUserId = requestUserId;
    }
}
