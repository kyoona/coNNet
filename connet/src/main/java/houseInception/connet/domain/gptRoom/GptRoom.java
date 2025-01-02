package houseInception.connet.domain.gptRoom;

import houseInception.connet.domain.BaseTime;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.Status.DELETED;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class GptRoom extends BaseTime {

    @Column(name = "GptRoomId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String gptRoomUuid;
    private String title;

    @OneToMany(mappedBy = "gptRoom", cascade = CascadeType.ALL)
    private List<GptRoomUser> gptRoomUsers = new ArrayList<>();

    @OneToMany(mappedBy = "gptRoom", cascade = CascadeType.ALL)
    private List<GptRoomChat> chats = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;

    public static GptRoom createGptRoom(User user){
        GptRoom gptRoom = new GptRoom();
        gptRoom.gptRoomUuid = UUID.randomUUID().toString();

        GptRoomUser gptRoomUser = new GptRoomUser(user, gptRoom);
        gptRoom.gptRoomUsers.add(gptRoomUser);

        return gptRoom;
    }

    public void updateGptRoom(String title){
        this.title = title;
    }

    public GptRoomChat addUserChat(GptRoomUser writer, String content){
        if(writer.getGptRoom().id.equals(this.id)){
            GptRoomChat chat = GptRoomChat.createUserChat(this, writer, content);
            chats.add(chat);
            return chat;
        }

        return null;
    }

    public GptRoomChat addGptChat(String content){
        GptRoomChat chat = GptRoomChat.createGptChat(this, content);
        chats.add(chat);

        return chat;
    }

    public void delete(){
        status = DELETED;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
