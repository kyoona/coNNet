package houseInception.connet.dto.group;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupUserResDto {

    private Long userId;
    private String userName;
    private String userProfile;
    private boolean isActive;
    private boolean isOwner;

    @QueryProjection

    public GroupUserResDto(Long userId, String userName, String userProfile, boolean isActive, boolean isOwner) {
        this.userId = userId;
        this.userName = userName;
        this.userProfile = userProfile;
        this.isActive = isActive;
        this.isOwner = isOwner;
    }
}
