package houseInception.connet.domain.user;

import houseInception.connet.domain.BaseTime;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.UserRole;
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
public class User extends BaseTime {

    @Column(name = "userId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String userProfile;
    private String userDescription;
    private String email;
    private String refreshToken;
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Embedded
    private Setting setting;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;

    public static User create(String userName, String userProfile, String email, String refreshToken){
        User user = new User();
        user.userName = userName;
        user.userProfile = userProfile;
        user.email = email;
        user.refreshToken = refreshToken;
        user.role = USER;
        user.setting = new Setting(false);
        return user;
    }

    public void update(String userName, String userProfile, String userDescription){
        this.userName = userName;
        this.userProfile = userProfile;
        this.userDescription = userDescription;
    }

    public void delete(){
        this.status = Status.DELETED;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void deleteRefreshToken(){
        this.refreshToken = null;
    }

    public void setActive(){
        isActive = true;
    }

    public void setInActive(){
        isActive = false;
    }
}
