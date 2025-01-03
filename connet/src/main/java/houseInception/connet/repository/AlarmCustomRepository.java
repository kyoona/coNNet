package houseInception.connet.repository;

import houseInception.connet.dto.alarm.AlarmCountResDto;

public interface AlarmCustomRepository {

    AlarmCountResDto getUncheckedAlarmCount(Long userId);
}
