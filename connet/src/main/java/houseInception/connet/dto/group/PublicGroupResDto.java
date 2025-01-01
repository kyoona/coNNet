package houseInception.connet.dto.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Long lastChatMin;

    @QueryProjection
    public PublicGroupResDto(Long groupId, String groupUuid, String groupName, String groupProfile, String groupDescription, String tagsStr, int userLimit, LocalDateTime lastChatTime, Long groupUserId) {
        this.groupId = groupId;
        this.groupUuid = groupUuid;
        this.groupName = groupName;
        this.groupProfile = groupProfile;
        this.groupDescription = groupDescription;
        this.userLimit = userLimit;
        this.isParticipate = groupUserId == null ? false : true;

        setLastChatMin(lastChatTime);
        setTags(tagsStr);
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount.intValue();
    }

    private void setTags(String tagsStr) {
        this.tags = tagsStr == null
                ? new ArrayList<>()
                : Arrays.stream(tagsStr.split(",")).toList();
    }

    private void setLastChatMin(LocalDateTime lastChatTime) {
        this.lastChatMin = lastChatTime == null
                ? null
                :ChronoUnit.MINUTES.between(lastChatTime, LocalDateTime.now());
    }
}
