package houseInception.connet.contoller;

import houseInception.connet.dto.alarm.AlarmCountResDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/alarms")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/count")
    public BaseResponse<AlarmCountResDto> getAlarmCount(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        AlarmCountResDto result = alarmService.getAlarmCount(userId);

        return new BaseResponse<>(result);
    }
}
