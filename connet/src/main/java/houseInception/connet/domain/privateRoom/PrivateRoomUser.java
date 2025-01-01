package houseInception.connet.domain.privateRoom;

import houseInception.connet.domain.BaseTime;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.user.User;
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

    protected PrivateRoomUser(User user, PrivateRoom privateRoom){
        this.user = user;
        this.privateRoom = privateRoom;
        this.participationTime = LocalDateTime.now();
    }

    protected void setStatus(Status status) {
        this.status = status;
    }

    protected void setParticipationTime(LocalDateTime participationTime) {
        this.participationTime = participationTime;
    }
}
