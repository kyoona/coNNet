package houseInception.connet.repository;

import houseInception.connet.dto.alarm.AlarmCountResDto;

public interface AlarmCustomRepository {

    void deleteAlarmOver7days();

    AlarmCountResDto getUncheckedAlarmCount(Long userId);
}
