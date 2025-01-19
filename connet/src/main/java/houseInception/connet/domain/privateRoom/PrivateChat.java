package houseInception.connet.domain.privateRoom;

import houseInception.connet.domain.BaseTime;
import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static houseInception.connet.domain.ChatterRole.GPT;
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
    @ManyToOne(fetch = FetchType.LAZY)
    private PrivateRoom privateRoom;

    @JoinColumn(name = "writerId")
    @ManyToOne(fetch = FetchType.LAZY)
    private PrivateRoomUser writer;

    @Enumerated(EnumType.STRING)
    private ChatterRole writerRole;

    @Enumerated(EnumType.STRING)
    private ChatterRole chatTarget;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String message;
    private String image;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;

    protected static PrivateChat createUserToUserChat
            (PrivateRoom privateRoom, PrivateRoomUser privateRoomUser, String message, String imgUrl){
        PrivateChat privateChat = new PrivateChat();
        privateChat.privateRoom = privateRoom;
        privateChat.writer = privateRoomUser;
        privateChat.writerRole = USER;
        privateChat.chatTarget = USER;
        privateChat.message =  message;
        privateChat.image = imgUrl;

        return privateChat;
    }

    protected static PrivateChat createUserToGptChat(PrivateRoom privateRoom, PrivateRoomUser privateRoomUser, String message) {
        PrivateChat privateChat = new PrivateChat();
        privateChat.privateRoom = privateRoom;
        privateChat.writer = privateRoomUser;
        privateChat.writerRole = USER;
        privateChat.chatTarget = GPT;
        privateChat.message =  message;

        return privateChat;
    }

    protected static PrivateChat createGptToUserChat(PrivateRoom privateRoom, String message) {
        PrivateChat privateChat = new PrivateChat();
        privateChat.privateRoom = privateRoom;
        privateChat.writerRole = GPT;
        privateChat.chatTarget = USER;
        privateChat.message =  message;

        return privateChat;
    }
}
