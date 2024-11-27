package houseInception.connet.repository;

import houseInception.connet.domain.ChatEmoji;
import houseInception.connet.domain.ChatRoomType;
import houseInception.connet.domain.EmojiType;

import java.util.Optional;

public interface ChatEmojiCustomRepository {

    Optional<ChatEmoji> findChatEmoji(Long userId, Long chatId, EmojiType emojiType, ChatRoomType chatRoomType);
    boolean existsEmojiInChat(Long userId, Long chatId, EmojiType emojiType, ChatRoomType chatRoomType);
}
