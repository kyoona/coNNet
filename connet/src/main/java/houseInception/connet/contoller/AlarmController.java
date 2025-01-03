package houseInception.connet.contoller;

import houseInception.connet.dto.alarm.AlarmCountResDto;
import houseInception.connet.dto.alarm.AlarmResDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController("/alarms")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/count")
    public BaseResponse<AlarmCountResDto> getAlarmCount(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        AlarmCountResDto result = alarmService.getUncheckedAlarmCount(userId);

        return new BaseResponse<>(result);
    }

    @GetMapping
    public BaseResponse<List<AlarmResDto>> getAlarmList(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        List<AlarmResDto> alarmList = alarmService.getAlarmList(userId);

        return new BaseResponse<>(alarmList);
    }
}
