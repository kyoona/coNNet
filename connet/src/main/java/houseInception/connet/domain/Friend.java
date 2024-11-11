package houseInception.connet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.connet.domain.FriendStatus.ACCEPT;
import static houseInception.connet.domain.FriendStatus.WAIT;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class Friend extends BaseTime{

    @Column(name = "friendId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "senderId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @JoinColumn(name = "recipientId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FriendStatus acceptStatus = WAIT;

    public static Friend createFriend(User user, User targetUser){
        Friend friend = new Friend();
        friend.sender = user;
        friend.receiver = targetUser;

        return friend;
    }

    public void accept(){
        this.acceptStatus = ACCEPT;
    }
}
