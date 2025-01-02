package houseInception.connet.dto.user;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class CommonGroupOfUserResDto {

    private String groupUuid;
    private String groupName;
    private String groupProfile;

    @QueryProjection
    public CommonGroupOfUserResDto(String groupUuid, String groupName, String groupProfile) {
        this.groupUuid = groupUuid;
        this.groupName = groupName;
        this.groupProfile = groupProfile;
    }
}
