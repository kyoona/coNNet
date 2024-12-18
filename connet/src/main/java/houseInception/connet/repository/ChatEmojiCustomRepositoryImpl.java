package houseInception.connet.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.*;
import houseInception.connet.dto.chatEmoji.ChatEmojiUserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static houseInception.connet.domain.QChatEmoji.chatEmoji;
import static houseInception.connet.domain.QUser.user;

@RequiredArgsConstructor
@Repository
public class ChatEmojiCustomRepositoryImpl implements ChatEmojiCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public Optional<ChatEmoji> findChatEmoji(Long userId, Long chatId, EmojiType emojiType, ChatRoomType chatRoomType) {
        ChatEmoji findChatEmoji = query
                .selectFrom(chatEmoji)
                .where(chatEmoji.user.id.eq(userId),
                        chatEmoji.emojiType.eq(emojiType),
                        chatEmoji.chatId.eq(chatId),
                        chatEmoji.chatRoomType.eq(chatRoomType))
                .fetchOne();

        return Optional.ofNullable(findChatEmoji);
    }

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

    @Override
    public List<ChatEmojiUserResDto> getEmojiUsers(Long chatId, EmojiType emojiType, ChatRoomType chatRoomType) {
        return query
                .select(Projections.constructor(
                        ChatEmojiUserResDto.class,
                        user.userName
                ))
                .from(chatEmoji)
                .innerJoin(chatEmoji.user, user)
                .where(chatEmoji.chatId.eq(chatId),
                        chatEmoji.emojiType.eq(emojiType),
                        chatEmoji.chatRoomType.eq(chatRoomType))
                .orderBy(user.userName.asc())
                .fetch();
    }
}
