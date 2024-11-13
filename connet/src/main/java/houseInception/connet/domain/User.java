package houseInception.connet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.UserRole.USER;

@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class User extends BaseTime{

    @Column(name = "userId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String userProfile;
    private String email;
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;

    public static User create(String userName, String userProfile, String email, String refreshToken){
        User user = new User();
        user.userName = userName;
        user.userProfile = userProfile;
        user.email = email;
        user.refreshToken = refreshToken;
        user.role = USER;
        return user;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void deleteRefreshToken(){
        this.refreshToken = null;
    }
}
