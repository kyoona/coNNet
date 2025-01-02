package houseInception.connet.dto;

import com.querydsl.core.annotations.QueryProjection;
import houseInception.connet.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DefaultUserResDto {

    private Long userId;
    private String userName;
    private String userProfile;

    @QueryProjection
    public DefaultUserResDto(Long userId, String userName, String userProfile) {
        this.userId = userId;
        this.userName = userName;
        this.userProfile = userProfile;
    }

    public DefaultUserResDto(User user) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.userProfile = user.getUserProfile();
    }
}
