package houseInception.connet.domain.alarm;

import houseInception.connet.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("Friend")
@Entity
public class FriendAlarm extends Alarm{

    @JoinColumn(name = "friendRequesterId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User friendRequester;

    private FriendAlarm(AlarmType alarmType, User user) {
        super(alarmType, user);
    }

    public static FriendAlarm createRequestAlarm(User user, User friendRequester){
        FriendAlarm alarm = new FriendAlarm(AlarmType.FRIEND_REQUEST, user);
        alarm.friendRequester = friendRequester;

        return alarm;
    }
}
