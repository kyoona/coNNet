package houseInception.gptComm.domain.chatRoom;

import houseInception.gptComm.domain.BaseTime;
import houseInception.gptComm.domain.ChatterRole;
import houseInception.gptComm.domain.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.gptComm.domain.ChatterRole.GPT;
import static houseInception.gptComm.domain.ChatterRole.USER;
import static houseInception.gptComm.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class Chat extends BaseTime {

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

    protected static Chat createUserChatToGpt(ChatRoom chatRoom, ChatRoomUser writer, String content){
        Chat chat = new Chat();
        chat.chatRoom = chatRoom;
        chat.writer = writer;
        chat.writerRole = USER;
        chat.content = content;
        chat.chatTarget = GPT;

        return chat;
    }

    protected static Chat createGptChat(ChatRoom chatRoom, String content){
        Chat chat = new Chat();
        chat.chatRoom = chatRoom;
        chat.writer = null;
        chat.writerRole = GPT;
        chat.content = content;
        chat.chatTarget = USER;

        return chat;
    }
}
