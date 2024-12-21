package houseInception.connet.event.handler;

import houseInception.connet.event.domain.GroupInviteAcceptEvent;
import houseInception.connet.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventGroupHandler {

    public final GroupService groupService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void acceptGroupInvite(GroupInviteAcceptEvent event){
        groupService.addGroupUser(event.getUserId(), event.getGroupUuid());
    }
}
