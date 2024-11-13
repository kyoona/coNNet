package houseInception.connet.dto;

import com.querydsl.core.annotations.QueryProjection;
import houseInception.connet.domain.FriendStatus;
import houseInception.connet.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.connet.dto.FriendType.*;

@Getter
@NoArgsConstructor
public class UserResDto {

    private Long userId;
    private String userName;
    private String userProfile;
    private FriendType friendType = NONE;

    @QueryProjection
    public UserResDto(Long userId, String userName, String userProfile, Long friendSenderId, FriendStatus friendStatus) {
        this.userId = userId;
        this.userName = userName;
        this.userProfile = userProfile;

        if(friendSenderId != null){
            if (friendStatus == FriendStatus.ACCEPT) {
                friendType = FRIEND;
            } else if (friendSenderId.equals(userId)){
                friendType = WAIT;
            } else {
                friendType = REQUEST;
            }
        }
    }

    public UserResDto(User user) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.userProfile = user.getUserProfile();
    }
}
