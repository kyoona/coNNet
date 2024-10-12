package houseInception.gptComm.dto;

import com.querydsl.core.annotations.QueryProjection;
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
}
