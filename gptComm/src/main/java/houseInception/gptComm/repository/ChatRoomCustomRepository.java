package houseInception.gptComm.repository;

import houseInception.gptComm.domain.Status;
import houseInception.gptComm.domain.chatRoom.Chat;

import java.util.List;

public interface ChatRoomCustomRepository {

    List<Chat> getChatListOfChatRoom(Long chatRoomId);
    boolean existsChatRoomUser(Long chatRoomId, Long userId, Status status);
}
