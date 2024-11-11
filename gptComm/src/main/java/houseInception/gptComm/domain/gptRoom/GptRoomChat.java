package houseInception.gptComm.domain.gptRoom;

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
public class GptRoomChat extends BaseTime {

    @Column(name = "GptRoomChatId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "chatRoomId")
    @ManyToOne(fetch = FetchType.LAZY)
    private GptRoom gptRoom;

    @JoinColumn(name = "writerId")
    @ManyToOne(fetch = FetchType.LAZY)
    private GptRoomUser writer;

    @Enumerated(EnumType.STRING)
    private ChatterRole writerRole;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private ChatterRole chatTarget;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;

    protected static GptRoomChat createUserChat(GptRoom gptRoom, GptRoomUser writer, String content){
        GptRoomChat chat = new GptRoomChat();
        chat.gptRoom = gptRoom;
        chat.writer = writer;
        chat.writerRole = USER;
        chat.content = content;
        chat.chatTarget = GPT;

        return chat;
    }

    protected static GptRoomChat createGptChat(GptRoom gptRoom, String content){
        GptRoomChat chat = new GptRoomChat();
        chat.gptRoom = gptRoom;
        chat.writer = null;
        chat.writerRole = GPT;
        chat.content = content;
        chat.chatTarget = USER;

        return chat;
    }
}
