package houseInception.connet.event.handler;

import houseInception.connet.event.domain.GroupAddEvent;
import houseInception.connet.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventChannelHandler {

    private final ChannelService channelService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void addDefaultChannelTap(GroupAddEvent event){
        channelService.addDefaultChannelTap(event.getGroupId());
    }
}
