package houseInception.connet.event.handler;

import houseInception.connet.event.domain.alarmEvent.FriendAlarmEvent;
import houseInception.connet.event.domain.alarmEvent.GroupAlarmEvent;
import houseInception.connet.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class EventAlarmHandler {

    private final AlarmService alarmService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFriendAlarm(FriendAlarmEvent event){
        alarmService.createFriendAlarm(event.getAlarmType(), event.getAlarmUserId(), event.getRequestUserId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGroupAlarm(GroupAlarmEvent event){
        alarmService.createGroupAlarm(event.getAlarmType(), event.getAlarmUserId(), event.getGroupUuid());
    }
}
