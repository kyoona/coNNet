package houseInception.connet.dto.user;

import com.querydsl.core.annotations.QueryProjection;
import houseInception.connet.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileResDto {

    private Long userId;
    private String userName;
    private String userProfile;
    private String userDescription;

    @QueryProjection
    public UserProfileResDto(Long userId, String userName, String userProfile, String userDescription) {
        this.userId = userId;
        this.userName = userName;
        this.userProfile = userProfile;
        this.userDescription = userDescription;
    }

    public UserProfileResDto(User user) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.userProfile = user.getUserProfile();
        this.userDescription = user.getUserDescription();
    }
}
