package houseInception.connet.repository;

import houseInception.connet.domain.ChatEmoji;
import houseInception.connet.domain.ChatRoomType;
import houseInception.connet.domain.EmojiType;
import houseInception.connet.dto.chatEmoji.ChatEmojiUserResDto;

import java.util.List;
import java.util.Optional;

public interface ChatEmojiCustomRepository {

    Optional<ChatEmoji> findChatEmoji(Long userId, Long chatId, EmojiType emojiType, ChatRoomType chatRoomType);

    boolean existsEmojiInChat(Long userId, Long chatId, EmojiType emojiType, ChatRoomType chatRoomType);

    List<ChatEmojiUserResDto> getEmojiUsers(Long chatId, EmojiType emojiType, ChatRoomType chatRoomType);
}
