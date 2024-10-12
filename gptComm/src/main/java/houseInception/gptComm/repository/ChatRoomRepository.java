package houseInception.gptComm.repository;

import houseInception.gptComm.domain.Status;
import houseInception.gptComm.domain.chatRoom.ChatRoom;
import houseInception.gptComm.domain.chatRoom.ChatRoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomCustomRepository {

    Optional<ChatRoom> findByChatRoomUuidAndStatus(String chatRoomUuid, Status status);

    boolean existsByChatRoomUuidAndChatRoomTypeAndStatus(String chatRoomUuid, ChatRoomType chatRoomType, Status status);
}
