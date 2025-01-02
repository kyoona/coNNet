package houseInception.connet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class ChatReadLog extends BaseTime{

    @Column(name = "chatReadLogId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChatRoomType type;
    private Long userId;
    private Long tapId;
    private String privateRoomUuid;
    private Long chatId;

    public static ChatReadLog createGroupChatLog(Long userId, Long tapId, Long chatId){
        ChatReadLog readLog = new ChatReadLog();
        readLog.userId = userId;
        readLog.type = ChatRoomType.GROUP;
        readLog.tapId = tapId;
        readLog.chatId = chatId;

        return readLog;
    }

    public static ChatReadLog createPrivateChatLog(Long userId, String privateRoomUuid, Long chatId){
        ChatReadLog readLog = new ChatReadLog();
        readLog.userId = userId;
        readLog.type = ChatRoomType.PRIVATE;
        readLog.privateRoomUuid = privateRoomUuid;
        readLog.chatId = chatId;

        return readLog;
    }
}
