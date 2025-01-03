package houseInception.connet.domain.alarm;

import houseInception.connet.domain.group.Group;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("Friend")
@Entity
public class GroupAlarm extends Alarm{

    @JoinColumn(name = "groupId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    private GroupAlarm(AlarmType alarmType) {
        super(alarmType);
    }

    public static GroupAlarm createInviteAlarm(Group group){
        GroupAlarm alarm = new GroupAlarm(AlarmType.GROUP_INVITE);
        alarm.group = group;
        
        return alarm;
    }
}
