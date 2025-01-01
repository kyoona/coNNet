package houseInception.connet.domain;

import houseInception.connet.domain.group.GroupUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.connet.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class GroupChat extends BaseTime{

    @Column(name = "groupChatId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "writerId")
    @ManyToOne(fetch = FetchType.LAZY)
    private GroupUser writer;

    @Enumerated(EnumType.STRING)
    private ChatterRole writerRole;

    private Long groupId;
    private Long tapId;
    private String message;
    private String image;

    @Enumerated(EnumType.STRING)
    private ChatterRole chatTarget;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;

    public static GroupChat createUserToUser(Long groupId, GroupUser writer, Long tapId, String message, String image){
        GroupChat chat = new GroupChat();
        chat.groupId = groupId;
        chat.writer = writer;
        chat.writerRole = ChatterRole.USER;
        chat.tapId = tapId;
        chat.message = message;
        chat.image = image;
        chat.chatTarget = ChatterRole.USER;

        return chat;
    }

    public static GroupChat createUserToGpt(Long groupId, GroupUser writer, Long tapId, String message){
        GroupChat chat = new GroupChat();
        chat.groupId = groupId;
        chat.writer = writer;
        chat.writerRole = ChatterRole.USER;
        chat.tapId = tapId;
        chat.message = message;
        chat.chatTarget = ChatterRole.GPT;

        return chat;
    }

    public static GroupChat createGptToUser(Long groupId, Long tapId, String message){
        GroupChat chat = new GroupChat();
        chat.groupId = groupId;
        chat.writerRole = ChatterRole.GPT;
        chat.tapId = tapId;
        chat.message = message;
        chat.chatTarget = ChatterRole.USER;

        return chat;
    }
}
