package houseInception.connet.socketManager.dto;

import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static houseInception.connet.socketManager.dto.ChatMessageType.PRIVATE;

@Getter
public class PrivateChatResDto {

    private ChatMessageType type = PRIVATE;
    private String chatRoomUuid;
    private String message;
    private String image;
    private ChatterRole writerRole;
    private ChatterResDto writer;
    private LocalDateTime createAt;

    public PrivateChatResDto(String chatRoomUuid, String message, String image, ChatterRole writerRole, User user, LocalDateTime createAt) {
        this.message = message;
        this.image = image;
        this.writerRole = writerRole;
        this.writer = new ChatterResDto(user.getId(), user.getUserName(), user.getUserProfile());
        this.createAt = createAt;
    }
}
