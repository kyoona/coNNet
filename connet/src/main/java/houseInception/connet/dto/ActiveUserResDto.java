package houseInception.connet.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ActiveUserResDto {

    private Long userId;
    private String userName;
    private String userProfile;
    private boolean isActive;

    @QueryProjection
    public ActiveUserResDto(Long userId, String userName, String userProfile, boolean isActive) {
        this.userId = userId;
        this.userName = userName;
        this.userProfile = userProfile;
        this.isActive = isActive;
    }
}
