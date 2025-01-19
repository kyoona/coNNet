package houseInception.connet.dto.channel;

import houseInception.connet.repository.dto.ChannelTapDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@NoArgsConstructor
public class ChannelResDto {

    private Long channelId;
    private String channelName;
    private List<TapResDto> taps = new ArrayList<>();

    public ChannelResDto(List<ChannelTapDto> channelTapDto, Map<Long, Long> recentChatOfTaps, Map<Long, Long> recentReadLogOfTaps) {
        ChannelTapDto channelInfo = channelTapDto.get(0);

        this.channelId = channelInfo.getChannelId();
        this.channelName = channelInfo.getChannelName();

        channelTapDto.forEach((dto) -> {
            log.info("tap id = {} / recent chat id = {}/ recent read id = {}", dto.getTapId(), recentChatOfTaps.get(dto.getTapId()), recentReadLogOfTaps.get(dto.getTapId()));
            if(dto.getTapId() != null){
                boolean isAllread = (recentChatOfTaps.get(dto.getTapId()) == null)
                        || (recentChatOfTaps.get(dto.getTapId()).equals(recentReadLogOfTaps.get(dto.getTapId())));
                this.taps.add(new TapResDto(dto.getTapId(), dto.getTapName(), !isAllread));
            }
        });
    }
}
