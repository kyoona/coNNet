package houseInception.connet.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrivateRoomResDto {

    @JsonIgnore
    private Long chatRoomId;
    private String chatRoomUuid;
    private Long userId;
    private String userName;
    private String userProfile;
    private boolean isActive;
    private boolean isBlock;

    @QueryProjection
    public PrivateRoomResDto(Long chatRoomId, String chatRoomUuid, Long userId, String userName, String userProfile, boolean isActive, Long blockId) {
        this.chatRoomId = chatRoomId;
        this.chatRoomUuid = chatRoomUuid;
        this.userId = userId;
        this.userName = userName;
        this.userProfile = userProfile;
        this.isActive = isActive;
        this.isBlock = blockId == null? false : true;
    }
}
