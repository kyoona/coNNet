package houseInception.gptComm.domain.chatRoom;

import houseInception.gptComm.domain.BaseTime;
import houseInception.gptComm.domain.Status;
import houseInception.gptComm.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.gptComm.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class ChatRoomUser extends BaseTime {

    @Column(name = "chatRoomUserId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "chatRoomId")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @Enumerated(value = EnumType.STRING)
    private Status status = ALIVE;

    protected ChatRoomUser(User user, ChatRoom chatRoom) {
        this.user = user;
        this.chatRoom = chatRoom;
    }
}
