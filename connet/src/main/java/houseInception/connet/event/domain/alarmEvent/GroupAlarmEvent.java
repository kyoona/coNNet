package houseInception.connet.event.domain.alarmEvent;

public class GroupAlarmEvent extends AlarmEvent{

    private String groupUuid;

    public GroupAlarmEvent(Long userId, String groupUuid) {
        super(userId);
        this.groupUuid = groupUuid;
    }
}
