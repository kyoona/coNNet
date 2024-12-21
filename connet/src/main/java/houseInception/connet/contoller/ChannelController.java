package houseInception.connet.contoller;

import houseInception.connet.dto.channel.ChannelDto;
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
                                                 @RequestBody @Valid ChannelDto channelDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = channelService.addChannel(userId, groupUuid, channelDto);

        return BaseResponse.getSimpleRes(resultId);
    }

    @PatchMapping("/{channelId}")
    public BaseResponse<DefaultIdDto> updateChannel(@PathVariable String groupUuid,
                                                    @PathVariable Long channelId,
                                                    @RequestBody @Valid ChannelDto channelDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = channelService.updateChannel(userId, groupUuid, channelId, channelDto);

        return BaseResponse.getSimpleRes(resultId);
    }

    @DeleteMapping("/{channelId}")
    public BaseResponse<DefaultIdDto> deleteChannel(@PathVariable String groupUuid,
                                                    @PathVariable Long channelId){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = channelService.deleteChannel(userId, groupUuid, channelId);

        return BaseResponse.getSimpleRes(resultId);
    }
}
