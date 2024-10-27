package houseInception.gptComm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.gptComm.domain.FriendStatus.ACCEPT;
import static houseInception.gptComm.domain.FriendStatus.WAIT;

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
    private User recipient;

    @Enumerated(EnumType.STRING)
    private FriendStatus acceptStatus = WAIT;

    public static Friend createFriend(User user, User targetUser){
        Friend friend = new Friend();
        friend.sender = user;
        friend.recipient = targetUser;

        return friend;
    }

    public void accept(){
        this.acceptStatus = ACCEPT;
    }
}
