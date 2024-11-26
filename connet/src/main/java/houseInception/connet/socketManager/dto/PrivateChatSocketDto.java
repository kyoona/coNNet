package houseInception.connet.socketManager.dto;

import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

import static houseInception.connet.socketManager.dto.ChatMessageType.PRIVATE;

@Getter
public class PrivateChatSocketDto {

    private ChatMessageType type = PRIVATE;
    private String chatRoomUuid;
    private Long chatId;
    private String message;
    private String image;
    private ChatterRole writerRole;
    private ChatterResDto writer;
    private LocalDateTime createAt;

    public PrivateChatSocketDto(String chatRoomUuid, Long chatId, String message, String image, ChatterRole writerRole, User user, LocalDateTime createAt) {
        this.chatRoomUuid = chatRoomUuid;
        this.chatId = chatId;
        this.message = message;
        this.image = image;
        this.writerRole = writerRole;
        this.writer = new ChatterResDto(user.getId(), user.getUserName(), user.getUserProfile());
        this.createAt = createAt;
    }
}
