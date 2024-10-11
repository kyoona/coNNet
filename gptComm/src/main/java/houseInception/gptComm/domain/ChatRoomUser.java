package houseInception.gptComm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static houseInception.gptComm.domain.Status.ALIVE;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class ChatRoomUser extends BaseTime{

    @Column(name = "chatRoomUserId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(value = EnumType.STRING)
    private Status status = ALIVE;
}
