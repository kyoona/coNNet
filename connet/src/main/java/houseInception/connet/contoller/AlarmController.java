package houseInception.connet.contoller;

import houseInception.connet.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/alarms")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/count")
    public void getAlarmCount(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        alarmService.getAlarmCount(userId);
    }
}
