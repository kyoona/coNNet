package houseInception.connet.event.publisher;

import houseInception.connet.domain.user.User;
import houseInception.connet.event.domain.UserBlockEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class UserBlockEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishUserBlockEvent(User user, User target){
        UserBlockEvent event = new UserBlockEvent(user.getId(), target.getId());
        publisher.publishEvent(event);
    }
}
