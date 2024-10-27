package houseInception.gptComm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.gptComm.domain.FriendStatus.WAIT;
import static houseInception.gptComm.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class Friend extends BaseTime{

    @Column(name = "friendId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "senderId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @JoinColumn(name = "recipientId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User recipient;

    @Enumerated(EnumType.STRING)
    private FriendStatus acceptStatus = WAIT;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;
}
