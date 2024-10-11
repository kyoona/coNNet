package houseInception.gptComm.domain.chatRoom;

import houseInception.gptComm.domain.BaseTime;
import houseInception.gptComm.domain.ChatterRole;
import houseInception.gptComm.domain.Status;
import houseInception.gptComm.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static houseInception.gptComm.domain.Status.ALIVE;
import static houseInception.gptComm.domain.chatRoom.ChatRoomType.GPT;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class ChatRoom extends BaseTime {

    @Column(name = "chatRoomId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatRoomUuid;
    private String title;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Chat> chats = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;

    public static ChatRoom createGptRoom(User user){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.chatRoomUuid = UUID.randomUUID().toString();
        chatRoom.chatRoomType = GPT;

        ChatRoomUser chatRoomUser = new ChatRoomUser(user, chatRoom);
        chatRoom.chatRoomUsers.add(chatRoomUser);

        return chatRoom;
    }

    public Chat addUserChatToGpt(ChatRoomUser writer, String content){
        if(writer.getChatRoom().id.equals(this.id) && this.chatRoomType.equals(GPT)){
            Chat chat = Chat.createUserChatToGpt(this, writer, content);
            chats.add(chat);
            return chat;
        }

        return null;
    }

    public Chat addGptChat(String content){
        Chat chat = Chat.createGptChat(this, content);
        chats.add(chat);

        return chat;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
