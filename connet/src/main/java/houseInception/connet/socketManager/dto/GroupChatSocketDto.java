package houseInception.connet.socketManager.dto;

import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

import static houseInception.connet.socketManager.dto.ChatMessageType.GROUP;

@Getter
public class GroupChatSocketDto {

    private ChatMessageType type = GROUP;
    private String groupUuid;
    private Long tapId;
    private Long chatId;
    private String message;
    private String image;
    private ChatterRole writerRole;
    private ChatterResDto writer;
    private LocalDateTime createAt;

    public GroupChatSocketDto(String groupUuid, Long tapId, Long chatId, String message, String image, ChatterRole writerRole, User user, LocalDateTime createAt) {
        this.groupUuid = groupUuid;
        this.tapId = tapId;
        this.chatId = chatId;
        this.message = message;
        this.image = image;
        this.writerRole = writerRole;
        this.writer = new ChatterResDto(user.getId(), user.getUserName(), user.getUserProfile());
        this.createAt = createAt;
    }
}
