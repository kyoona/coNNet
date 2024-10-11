package houseInception.gptComm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String googleToken;
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private Status status;
}
