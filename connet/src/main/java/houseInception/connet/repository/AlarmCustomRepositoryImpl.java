package houseInception.connet.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.dto.alarm.AlarmCountResDto;
import lombok.RequiredArgsConstructor;

import static houseInception.connet.domain.alarm.QAlarm.alarm;

@RequiredArgsConstructor
public class AlarmCustomRepositoryImpl implements AlarmCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public AlarmCountResDto getUncheckedAlarmCount(Long userId) {
        return query
                .select(Projections.constructor(
                        AlarmCountResDto.class,
                        alarm.count()
                ))
                .from(alarm)
                .where(
                        alarm.user.id.eq(userId),
                        alarm.isChecked.isFalse()
                )
                .fetchOne();
    }
}
