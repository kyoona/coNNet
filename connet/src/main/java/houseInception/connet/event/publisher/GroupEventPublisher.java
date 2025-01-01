package houseInception.connet.event.publisher;

import houseInception.connet.event.domain.GroupAddEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GroupEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishGroupAddEvent(Long groupId){
        GroupAddEvent event = new GroupAddEvent(groupId);
        publisher.publishEvent(event);
    }
}
