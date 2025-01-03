package houseInception.connet.service;

import houseInception.connet.domain.alarm.Alarm;
import houseInception.connet.domain.alarm.FriendAlarm;
import houseInception.connet.domain.alarm.GroupAlarm;
import houseInception.connet.dto.alarm.AlarmCountResDto;
import houseInception.connet.dto.alarm.AlarmResDto;
import houseInception.connet.dto.alarm.FriendAlarmResDto;
import houseInception.connet.dto.alarm.GroupAlarmResDto;
import houseInception.connet.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public AlarmCountResDto getUncheckedAlarmCount(Long userId) {
        return alarmRepository.getUncheckedAlarmCount(userId);
    }

    public List<AlarmResDto> getAlarmList(Long userId) {
        List<Alarm> alarmList = alarmRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<AlarmResDto> resDtoList = new ArrayList<>();
        alarmList.forEach((alarm) -> {
            if(alarm instanceof FriendAlarm){
                FriendAlarm friendAlarm = (FriendAlarm) alarm;
                resDtoList.add(new FriendAlarmResDto(friendAlarm, friendAlarm.getFriendRequester()));
            } else if (alarm instanceof GroupAlarm) {
                GroupAlarm groupAlarm = (GroupAlarm) alarm;
                resDtoList.add(new GroupAlarmResDto(groupAlarm, groupAlarm.getGroup()));
            }
            alarm.check();
        });

        return resDtoList;
    }
}
