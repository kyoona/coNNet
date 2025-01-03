package houseInception.connet.event.domain.alarmEvent;

public abstract class AlarmEvent {

    private Long userId;

    public AlarmEvent(Long userId) {
        this.userId = userId;
    }
}
