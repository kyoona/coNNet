package houseInception.connet.event.handler;

import houseInception.connet.event.domain.UserBlockEvent;
import houseInception.connet.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class UserBlockEventHandler {

    private final FriendService friendService;

    @Async
    @TransactionalEventListener
    public void deleteFriend(UserBlockEvent event){
        friendService.deleteFriend(event.getUserId(), event.getTargetId());
    }
}
