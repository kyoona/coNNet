package houseInception.connet.contoller;

import houseInception.connet.dto.channel.ChannelAddDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.DefaultIdDto;
import houseInception.connet.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/groups/{groupUuid}/channels")
@RequiredArgsConstructor
@RestController
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping
    public BaseResponse<DefaultIdDto> addChannel(@PathVariable String groupUuid,
                                                 @RequestBody @Valid ChannelAddDto channelAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = channelService.addChannel(userId, groupUuid, channelAddDto);

        return BaseResponse.getSimpleRes(resultId);
    }
}
