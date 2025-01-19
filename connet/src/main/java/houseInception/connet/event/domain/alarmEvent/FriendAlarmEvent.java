package houseInception.connet.event.domain.alarmEvent;

import houseInception.connet.domain.alarm.AlarmType;
import lombok.Getter;

@Getter
public class FriendAlarmEvent extends AlarmEvent{

    private Long requestUserId;

    public FriendAlarmEvent(AlarmType alarmType, Long userId, Long requestUserId) {
        super(alarmType, userId);
        this.requestUserId = requestUserId;
    }
}
