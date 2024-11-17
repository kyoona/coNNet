package houseInception.connet.domain.privateRoom;

import houseInception.connet.domain.BaseTime;
import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.connet.domain.ChatterRole.USER;
import static houseInception.connet.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class PrivateChat extends BaseTime {

    @Column(name = "privateChatId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "privateRoomId")
    @OneToOne(fetch = FetchType.LAZY)
    private PrivateRoom privateRoom;

    @JoinColumn(name = "writerId")
    @ManyToOne(fetch = FetchType.LAZY)
    private PrivateRoomUser writer;

    @Enumerated(EnumType.STRING)
    private ChatterRole writerRole;

    @Enumerated(EnumType.STRING)
    private ChatterRole chatTarget;

    private String message;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;

    protected static PrivateChat createUserToUserChat(PrivateRoom privateRoom, PrivateRoomUser privateRoomUser, String message){
        PrivateChat privateChat = new PrivateChat();
        privateChat.privateRoom = privateRoom;
        privateChat.writer = privateRoomUser;
        privateChat.writerRole = USER;
        privateChat.chatTarget = USER;
        privateChat.message =  message;

        return privateChat;
    }
}
