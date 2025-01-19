package houseInception.connet.dto.alarm;

import houseInception.connet.domain.alarm.AlarmType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class AlarmResDto {

    private AlarmType type;
    private boolean isChecked;
    private LocalDateTime createdAt;

    public AlarmResDto(AlarmType type, boolean isChecked, LocalDateTime createdAt) {
        this.type = type;
        this.isChecked = isChecked;
        this.createdAt = createdAt;
    }
}
