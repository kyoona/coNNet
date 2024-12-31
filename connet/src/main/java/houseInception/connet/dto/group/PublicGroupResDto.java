package houseInception.connet.dto.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.List;

@Getter
public class PublicGroupResDto {

    @JsonIgnore
    private Long groupId;
    private String groupUuid;
    private String groupName;
    private String groupProfile;
    private String groupDescription;
    private List<String> tags;
    private int userLimit;
    private int userCount;
    private boolean isParticipate;
    private Integer lastChatMin;

    @QueryProjection
    public PublicGroupResDto(Long groupId, String groupUuid, String groupName, String groupProfile, String groupDescription, List<String> tags, int userLimit, Integer lastChatMin, boolean isParticipate) {
        this.groupId = groupId;
        this.groupUuid = groupUuid;
        this.groupName = groupName;
        this.groupProfile = groupProfile;
        this.groupDescription = groupDescription;
        this.tags = tags;
        this.userLimit = userLimit;
        this.lastChatMin = lastChatMin;
        this.isParticipate = isParticipate;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount.intValue();
    }
}
