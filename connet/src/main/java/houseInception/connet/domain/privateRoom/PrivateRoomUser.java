package houseInception.connet.domain.privateRoom;

import houseInception.connet.domain.BaseTime;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static houseInception.connet.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class PrivateRoomUser extends BaseTime {

    @Column(name = "privateRoomUserId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "privateRoomId")
    @ManyToOne(fetch = FetchType.LAZY)
    private PrivateRoom privateRoom;

    private Long lastReadChatId;
    private LocalDateTime participationTime;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;
}
