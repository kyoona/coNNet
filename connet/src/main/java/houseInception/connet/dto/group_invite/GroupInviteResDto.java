package houseInception.connet.dto.group_invite;

import com.querydsl.core.annotations.QueryProjection;
import houseInception.connet.dto.DefaultUserResDto;
import lombok.Getter;

@Getter
public class GroupInviteResDto {

    private String groupName;
    private String groupProfile;
    private DefaultUserResDto inviter;

    @QueryProjection

    public GroupInviteResDto(String groupName, String groupProfile, DefaultUserResDto inviter) {
        this.groupName = groupName;
        this.groupProfile = groupProfile;
        this.inviter = inviter;
    }
}
