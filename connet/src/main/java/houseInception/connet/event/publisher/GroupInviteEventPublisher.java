package houseInception.connet.event.publisher;

import houseInception.connet.event.domain.GroupInviteAcceptEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class GroupInviteEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishGroupInviteAcceptEvent(Long userId, String groupUuid){
        GroupInviteAcceptEvent event = new GroupInviteAcceptEvent(userId, groupUuid);
        publisher.publishEvent(event);
    }
}
