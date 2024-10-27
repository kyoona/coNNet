package houseInception.gptComm.dto;

import com.querydsl.core.annotations.QueryProjection;
import houseInception.gptComm.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResDto {

    private Long userId;
    private String userName;
    private String userProfile;

    @QueryProjection
    public UserResDto(Long userId, String userName, String userProfile) {
        this.userId = userId;
        this.userName = userName;
        this.userProfile = userProfile;
    }

    public UserResDto(User user) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.userProfile = user.getUserProfile();
    }
}
