package houseInception.connet.repository;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.gptRoom.GptRoomChat;
import houseInception.connet.dto.GptRoom.GptRoomChatResDto;
import houseInception.connet.dto.GptRoom.GptRoomListResDto;

import java.util.List;

public interface GptRoomCustomRepository {

    List<GptRoomChat> getChatListOfGptRoom(Long gptRoomId);

    boolean existsGptRoomUser(Long gptRoomId, Long userId, Status status);

    List<GptRoomListResDto> getGptRoomListByUserId(Long userId, int page);
    List<GptRoomChatResDto> getGptChatRoomChatList(Long gptRoomId, int page);
}
