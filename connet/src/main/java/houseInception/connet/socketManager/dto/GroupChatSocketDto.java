package houseInception.connet.socketManager.dto;

import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.GroupChat;
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

    public GroupChatSocketDto(String groupUuid, GroupChat chat, ChatterRole writerRole, User user) {
        this.groupUuid = groupUuid;
        this.tapId = chat.getTapId();
        this.chatId = chat.getId();
        this.message = chat.getMessage();
        this.image = chat.getImage();
        this.writerRole = writerRole;

        if(user != null){
            this.writer = new ChatterResDto(user.getId(), user.getUserName(), user.getUserProfile());
        }

        this.createAt = chat.getCreatedAt();
    }
}
