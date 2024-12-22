package houseInception.connet.dto.channel;

import houseInception.connet.repository.dto.ChannelTapDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ChannelResDto {

    private Long channelId;
    private String channelName;
    private List<TapResDto> taps = new ArrayList<>();

    public ChannelResDto(List<ChannelTapDto> channelTapDto) {
        ChannelTapDto channelInfo = channelTapDto.get(0);

        this.channelId = channelInfo.getChannelId();
        this.channelName = channelInfo.getChannelName();

        channelTapDto.forEach((dto) -> {
            if(dto.getTapId() != null){
                this.taps.add(new TapResDto(dto.getTapId(), dto.getTapName()));
            }
        });
    }
}
