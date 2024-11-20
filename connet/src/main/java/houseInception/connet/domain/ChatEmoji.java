package houseInception.connet.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.connet.domain.ChatRoomType.PRIVATE;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class ChatEmoji extends BaseTime{

    @Column(name = "chatEmojiId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Long chatId;

    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType;

    @Enumerated(EnumType.STRING)
    private EmojiType emojiType;

    public static ChatEmoji createPrivateChatEmoji(User user, Long chatId, EmojiType emojiType){
        ChatEmoji chatEmoji = new ChatEmoji();
        chatEmoji.user = user;
        chatEmoji.chatId = chatId;
        chatEmoji.chatRoomType = PRIVATE;
        chatEmoji.emojiType = emojiType;

        return chatEmoji;
    }
}
