package houseInception.connet.domain.privateRoom;

import houseInception.connet.domain.BaseTime;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static houseInception.connet.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class PrivateRoom extends BaseTime {

    @Column(name = "privateRoomId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @OneToMany(mappedBy = "privateRoom", cascade = CascadeType.ALL)
    private List<PrivateRoomUser> privateRoomUsers = new ArrayList<>();

    private String privateRoomUuid;

    @OneToMany(mappedBy = "privateRoom", cascade = CascadeType.ALL)
    private List<PrivateChat> privateChats = new ArrayList<>();


    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;

    public static PrivateRoom create(User user, User targetUser){
        PrivateRoom privateRoom = new PrivateRoom();
        privateRoom.privateRoomUuid = UUID.randomUUID().toString();
        privateRoom.privateRoomUsers.add(new PrivateRoomUser(user, privateRoom));
        privateRoom.privateRoomUsers.add(new PrivateRoomUser(targetUser, privateRoom));

        return privateRoom;
    }

    public PrivateChat addUserToUserChat(String message, String imgUrl, PrivateRoomUser privateRoomUser){
        PrivateChat chat = PrivateChat.createUserToUserChat(this, privateRoomUser, message, imgUrl);
        this.privateChats.add(chat);

        return chat;
    }

    public List<PrivateChat> getPrivateChats() {
        return new ArrayList<>(privateChats);
    }

    public List<PrivateRoomUser> getPrivateRoomUsers() {
        return new ArrayList<>(privateRoomUsers);
    }

    public void setPrivateRoomUserAlive(PrivateRoomUser privateRoomUser, LocalDateTime participationTime){
        if(privateRoomUser.getPrivateRoom().getId().equals(this.id)){
            privateRoomUser.setStatus(ALIVE);
            privateRoomUser.setParticipationTime(participationTime);
        }
    }
}
