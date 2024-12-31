package houseInception.connet.dto.group;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
public class GroupDetailResDto {

    private String groupUuid;
    private String groupName;
    private String groupProfile;
    private String groupDescription;
    private List<String> tags;
    private int userLimit;

    @QueryProjection
    public GroupDetailResDto(String groupUuid, String groupName, String groupProfile, String groupDescription, String tagsStr, int userLimit) {
        this.groupUuid = groupUuid;
        this.groupName = groupName;
        this.groupProfile = groupProfile;
        this.groupDescription = groupDescription;
        this.tags = Arrays.stream(tagsStr.split(",")).toList();
        this.userLimit = userLimit;
    }
}
