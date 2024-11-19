package houseInception.connet.repository;

import houseInception.connet.domain.ChatRoomType;
import houseInception.connet.domain.EmojiType;

public interface ChatEmojiCustomRepository {

    boolean existsEmojiInChat(Long userId, Long chatId, EmojiType emojiType, ChatRoomType chatRoomType);
}
