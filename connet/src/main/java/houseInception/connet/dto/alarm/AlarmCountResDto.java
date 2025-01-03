package houseInception.connet.dto.alarm;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlarmCountResDto {

    private int count;

    @QueryProjection
    public AlarmCountResDto(Long count) {
        this.count = count.intValue();
    }
}
