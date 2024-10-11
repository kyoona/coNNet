package houseInception.gptComm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static houseInception.gptComm.domain.Status.ALIVE;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class Chat extends BaseTime{

    @Column(name = "chatId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "chatRoomId")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @JoinColumn(name = "writerId")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoomUser writer;

    @Enumerated(EnumType.STRING)
    private ChatterRole writerRole;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private ChatterRole chatTarget;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;
}
