package houseInception.connet.dto.alarm;

import houseInception.connet.domain.alarm.AlarmType;
import houseInception.connet.domain.alarm.GroupAlarm;
import houseInception.connet.domain.group.Group;
import lombok.Getter;

@Getter
public class GroupAlarmResDto extends AlarmResDto{

    private String groupUuid;
    private String groupName;
    private String groupProfile;

    public GroupAlarmResDto(GroupAlarm alarm, Group group) {
        super(alarm.getAlarmType(), alarm.isChecked());
        this.groupUuid = group.getGroupUuid();
        this.groupName = group.getGroupName();
        this.groupProfile = group.getGroupProfile();
    }
}
