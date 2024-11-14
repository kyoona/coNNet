package houseInception.connet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class UserBlock extends BaseTime{

    @Column(name = "userBlockId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "targetId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User target;

    public static UserBlock create(User user, User target){
        UserBlock userBlock = new UserBlock();
        userBlock.user = user;
        userBlock.target = target;
        return userBlock;
    }
}
