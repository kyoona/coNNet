package houseInception.connet.domain;

import houseInception.connet.domain.privateRoom.PrivateChat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class PrivateChatEmoji extends BaseTime{

    @Column(name = "privateChatEmojiId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "privateChatId")
    @ManyToOne(fetch = FetchType.LAZY)
    private PrivateChat privateChat;
}
