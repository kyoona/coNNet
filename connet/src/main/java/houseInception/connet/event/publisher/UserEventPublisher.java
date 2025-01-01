package houseInception.connet.event.publisher;

import houseInception.connet.event.domain.UserDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishUserDeleteEvent(Long userId){
        UserDeleteEvent event = new UserDeleteEvent(userId);
        publisher.publishEvent(event);
    }
}
