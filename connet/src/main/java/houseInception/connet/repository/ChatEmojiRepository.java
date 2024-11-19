package houseInception.connet.repository;

import houseInception.connet.domain.ChatEmoji;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEmojiRepository extends JpaRepository<ChatEmoji, Long> , ChatEmojiCustomRepository{
}
