package houseInception.connet.socketManager.dto;

import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.user.User;
import houseInception.connet.domain.privateRoom.PrivateChat;
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

    public PrivateChatSocketDto(String chatRoomUuid, PrivateChat chat, ChatterRole writerRole, User user) {
        this.chatRoomUuid = chatRoomUuid;
        this.chatId = chat.getId();
        this.message = chat.getMessage();
        this.image = chat.getImage();
        this.writerRole = writerRole;

        if (user != null) {
            this.writer = new ChatterResDto(user.getId(), user.getUserName(), user.getUserProfile());
        }

        this.createAt = chat.getCreatedAt();
    }
}
