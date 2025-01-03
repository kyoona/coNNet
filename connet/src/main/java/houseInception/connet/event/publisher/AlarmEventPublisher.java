package houseInception.connet.event.publisher;

import houseInception.connet.domain.alarm.AlarmType;
import houseInception.connet.event.domain.alarmEvent.FriendAlarmEvent;
import houseInception.connet.event.domain.alarmEvent.GroupAlarmEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AlarmEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishFriendRequestAlarmEvent(Long alarmUserId, Long requestUserId){
        FriendAlarmEvent event = new FriendAlarmEvent(AlarmType.FRIEND_REQUEST, alarmUserId, requestUserId);
        publisher.publishEvent(event);
    }

    public void publishGroupInviteAlarmEvent(Long alarmUserId, String groupUuid){
        GroupAlarmEvent event = new GroupAlarmEvent(AlarmType.GROUP_INVITE, alarmUserId, groupUuid);
        publisher.publishEvent(event);
    }
}
