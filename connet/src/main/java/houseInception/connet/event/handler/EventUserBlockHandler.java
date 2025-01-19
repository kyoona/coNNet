package houseInception.connet.event.handler;

import houseInception.connet.event.domain.UserDeleteEvent;
import houseInception.connet.service.UserBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class EventUserBlockHandler {

    private final UserBlockService userBlockService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteAllUserBlockOfUser(UserDeleteEvent event){
        userBlockService.deleteAllUserBlockOfUser(event.getUserId());
    }
}
