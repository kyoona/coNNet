package houseInception.connet.domain.alarm;

import houseInception.connet.domain.group.Group;
import houseInception.connet.domain.user.User;
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

    private GroupAlarm(AlarmType alarmType, User user) {
        super(alarmType, user);
    }

    public static GroupAlarm createInviteAlarm(User user, Group group){
        GroupAlarm alarm = new GroupAlarm(AlarmType.GROUP_INVITE, user);
        alarm.group = group;

        return alarm;
    }
}
