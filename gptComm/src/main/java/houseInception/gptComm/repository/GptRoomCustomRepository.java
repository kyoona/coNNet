package houseInception.gptComm.repository;

import houseInception.gptComm.domain.Status;
import houseInception.gptComm.domain.gptRoom.GptRoomChat;
import houseInception.gptComm.dto.GptRoomChatResDto;
import houseInception.gptComm.dto.GptRoomListResDto;

import java.util.List;

public interface GptRoomCustomRepository {

    List<GptRoomChat> getChatListOfGptRoom(Long gptRoomId);
    boolean existsGptRoomUser(Long gptRoomId, Long userId, Status status);

    List<GptRoomListResDto> getGptRoomListByUserId(Long userId, int page);
    List<GptRoomChatResDto> getGptChatRoomChatList(Long gptRoomId, int page);
}
