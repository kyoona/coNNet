package houseInception.connet.service;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.alarm.Alarm;
import houseInception.connet.domain.alarm.AlarmType;
import houseInception.connet.domain.alarm.FriendAlarm;
import houseInception.connet.domain.alarm.GroupAlarm;
import houseInception.connet.domain.group.Group;
import houseInception.connet.domain.user.User;
import houseInception.connet.dto.alarm.AlarmCountResDto;
import houseInception.connet.dto.alarm.AlarmResDto;
import houseInception.connet.dto.alarm.FriendAlarmResDto;
import houseInception.connet.dto.alarm.GroupAlarmResDto;
import houseInception.connet.repository.AlarmRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

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

    @Transactional
    public void createFriendAlarm(AlarmType alarmType, Long alarmUserId, Long requestUserId) {
        User alarmUser = userRepository.findById(alarmUserId).get();
        User requestUser = userRepository.findById(requestUserId).get();

        FriendAlarm alarm;

        switch (alarmType){
            case FRIEND_REQUEST -> alarm = FriendAlarm.createRequestAlarm(alarmUser, requestUser);
            default -> throw new IllegalStateException("Not supported friend alarm type");
        }

        alarmRepository.save(alarm);
    }

    @Transactional
    public void createGroupAlarm(AlarmType alarmType, Long alarmUserId, String groupUuid) {
        User alarmUser = userRepository.findById(alarmUserId).get();
        Group group = groupRepository.findByGroupUuidAndStatus(groupUuid, Status.ALIVE).get();

        GroupAlarm alarm;

        switch (alarmType){
            case GROUP_INVITE -> alarm = GroupAlarm.createInviteAlarm(alarmUser, group);
            default -> throw new IllegalStateException("Not supported group alarm type");
        }

        alarmRepository.save(alarm);
    }
}
