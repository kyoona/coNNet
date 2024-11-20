package houseInception.connet.event.handler;

import houseInception.connet.event.domain.UserBlockEvent;
import houseInception.connet.service.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventFriendHandler {

    private final FriendService friendService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteFriend(UserBlockEvent event){
        log.info("이벤트핸들러!");
        friendService.deleteFriend(event.getUserId(), event.getTargetId());
    }
}
