package houseInception.connet.dto.privateRoom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

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
    private boolean isUnread;

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

    public void setUnread(Map<Long, Long> recentChatsOfRoom, Map<Long, Long> recentReadLogsOfRoom) {
        this.isUnread = !(recentChatsOfRoom.get(chatRoomId) == null || recentChatsOfRoom.get(chatRoomId).equals(recentReadLogsOfRoom.get(chatRoomId)));
    }
}
