package houseInception.connet.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.ChatRoomType;
import houseInception.connet.domain.EmojiType;
import houseInception.connet.domain.QChatEmoji;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static houseInception.connet.domain.QChatEmoji.chatEmoji;

@RequiredArgsConstructor
@Repository
public class ChatEmojiCustomRepositoryImpl implements ChatEmojiCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public boolean existsEmojiInChat(Long userId, Long chatId, EmojiType emojiType, ChatRoomType chatRoomType) {
        Long count = query.select(chatEmoji.count())
                .from(chatEmoji)
                .where(chatEmoji.user.id.eq(userId),
                        chatEmoji.chatId.eq(chatId),
                        chatEmoji.emojiType.eq(emojiType),
                        chatEmoji.chatRoomType.eq(chatRoomType))
                .fetchOne();

        return count != null && count > 0;
    }
}
