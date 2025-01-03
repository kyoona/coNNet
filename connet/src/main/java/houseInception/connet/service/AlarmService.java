package houseInception.connet.service;

import houseInception.connet.dto.alarm.AlarmCountResDto;
import houseInception.connet.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public AlarmCountResDto getUncheckedAlarmCount(Long userId) {
        return alarmRepository.getUncheckedAlarmCount(userId);
    }
}
