package houseInception.gptComm.repository;

import houseInception.gptComm.domain.Status;
import houseInception.gptComm.domain.chatRoom.Chat;
import houseInception.gptComm.dto.GptChatRoomChatResDto;
import houseInception.gptComm.dto.GptChatRoomListResDto;

import java.util.List;

public interface ChatRoomCustomRepository {

    List<Chat> getChatListOfChatRoom(Long chatRoomId);
    boolean existsChatRoomUser(Long chatRoomId, Long userId, Status status);

    List<GptChatRoomListResDto> getGptChatRoomListByUserId(Long userId, int page);
    List<GptChatRoomChatResDto> getGptChatRoomChatList(Long chatRoomId, int page);
}
