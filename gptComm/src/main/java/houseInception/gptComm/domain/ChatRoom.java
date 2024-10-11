package houseInception.gptComm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static houseInception.gptComm.domain.Status.ALIVE;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class ChatRoom extends BaseTime{

    @Column(name = "chatRoomId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatRoomUuid;
    private String title;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;
}
