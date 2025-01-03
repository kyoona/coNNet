package houseInception.connet.event.domain.alarmEvent;

import houseInception.connet.domain.alarm.AlarmType;
import lombok.Getter;

@Getter
public abstract class AlarmEvent {

    private AlarmType alarmType;
    private Long alarmUserId;

    public AlarmEvent(AlarmType alarmType, Long alarmUserId) {
        this.alarmType = alarmType;
        this.alarmUserId = alarmUserId;
    }
}
