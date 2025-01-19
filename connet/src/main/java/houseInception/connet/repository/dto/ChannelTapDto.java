package houseInception.connet.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ChannelTapDto {

    private Long channelId;
    private String channelName;
    private Long tapId;
    private String tapName;

    @QueryProjection
    public ChannelTapDto(Long channelId, String channelName, Long tapId, String tapName) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.tapId = tapId;
        this.tapName = tapName;
    }
}
