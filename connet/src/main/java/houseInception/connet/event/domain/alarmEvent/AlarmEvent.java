package houseInception.connet.event.domain.alarmEvent;

import lombok.Getter;

@Getter
public abstract class AlarmEvent {

    private Long userId;

    public AlarmEvent(Long userId) {
        this.userId = userId;
    }
}
