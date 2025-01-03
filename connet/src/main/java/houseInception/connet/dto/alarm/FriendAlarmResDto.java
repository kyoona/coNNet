package houseInception.connet.dto.alarm;

import houseInception.connet.domain.alarm.FriendAlarm;
import houseInception.connet.domain.user.User;
import lombok.Getter;

@Getter
public class FriendAlarmResDto extends AlarmResDto{

    private Long userId;
    private String userName;
    private String userProfile;

    public FriendAlarmResDto(FriendAlarm alarm, User requester) {
        super(alarm.getAlarmType(), alarm.isChecked(), alarm.getCreatedAt());
        this.userId = requester.getId();
        this.userName = requester.getUserName();
        this.userProfile = requester.getUserProfile();
    }
}
