package houseInception.connet.event.domain.alarmEvent;

import houseInception.connet.domain.alarm.AlarmType;
import lombok.Getter;

@Getter
public class GroupAlarmEvent extends AlarmEvent{

    private String groupUuid;

    public GroupAlarmEvent(AlarmType alarmType, Long userId, String groupUuid) {
        super(alarmType, userId);
        this.groupUuid = groupUuid;
    }
}
