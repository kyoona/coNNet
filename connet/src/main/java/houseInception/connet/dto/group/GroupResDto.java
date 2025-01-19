package houseInception.connet.dto.group;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class GroupResDto {

    private String groupUuid;
    private String groupName;
    private String groupProfile;

    @QueryProjection
    public GroupResDto(String groupUuid, String groupName, String groupProfile) {
        this.groupUuid = groupUuid;
        this.groupName = groupName;
        this.groupProfile = groupProfile;
    }
}
