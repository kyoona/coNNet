package houseInception.connet.domain.channel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class Channel {

    @Column(name = "channelId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupId;
    private String channelName;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelTap> tapList = new ArrayList<>();

    public static Channel create(Long groupId, String channelName) {
        Channel channel = new Channel();
        channel.groupId = groupId;
        channel.channelName = channelName;

        return channel;
    }

    public void update(String channelName){
        this.channelName = channelName;
    }

    public ChannelTap addTap(String tapName) {
        ChannelTap tap = new ChannelTap(this, tapName);
        this.tapList.add(tap);

        return tap;
    }
}
