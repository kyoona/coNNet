package houseInception.connet.domain.channel;

import houseInception.connet.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class ChannelTap extends BaseTime {

    @Column(name = "channelTapId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "channelId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Channel channel;

    private String tapName;

    protected ChannelTap(Channel channel, String tapName) {
        this.channel = channel;
        this.tapName = tapName;
    }

    protected void update(String tapName){
        this.tapName = tapName;
    }
}
