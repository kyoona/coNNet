package houseInception.connet.dto.alarm;

import houseInception.connet.domain.alarm.AlarmType;
import lombok.Getter;

@Getter
public abstract class AlarmResDto {

    private AlarmType type;
    private boolean isChecked;

    public AlarmResDto(AlarmType type, boolean isChecked) {
        this.type = type;
        this.isChecked = isChecked;
    }
}
