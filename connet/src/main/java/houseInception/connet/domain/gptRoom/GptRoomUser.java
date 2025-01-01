package houseInception.connet.domain.gptRoom;

import houseInception.connet.domain.BaseTime;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.connet.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class GptRoomUser extends BaseTime {

    @Column(name = "gptRoomUserId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "gptRoomId")
    @ManyToOne(fetch = FetchType.LAZY)
    private GptRoom gptRoom;

    @Enumerated(value = EnumType.STRING)
    private Status status = ALIVE;

    protected GptRoomUser(User user, GptRoom gptRoom) {
        this.user = user;
        this.gptRoom = gptRoom;
    }
}
