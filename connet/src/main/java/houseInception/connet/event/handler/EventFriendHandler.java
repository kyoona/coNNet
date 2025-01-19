package houseInception.connet.event.handler;

import houseInception.connet.event.domain.UserBlockEvent;
import houseInception.connet.event.domain.UserDeleteEvent;
import houseInception.connet.service.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
        friendService.deleteFriend(event.getUserId(), event.getTargetId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteAllFriendsOfUser(UserDeleteEvent event){
        friendService.deleteAllFriendsOfUser(event.getUserId());
    }
}
