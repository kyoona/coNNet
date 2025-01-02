package houseInception.connet.repository;

import houseInception.connet.domain.privateRoom.PrivateChat;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;
import houseInception.connet.dto.privateRoom.PrivateChatResDto;
import houseInception.connet.dto.privateRoom.PrivateRoomResDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PrivateRoomCustomRepository {

    Optional<PrivateRoom> findPrivateRoomWithUser(String privateRoomUuid);
    Optional<PrivateRoom> findPrivateRoomByUsers(Long userId, Long targetId);
    Optional<PrivateRoomUser> findPrivateRoomUser(Long privateRoomId, Long userId);
    List<PrivateChat> findPrivateChatsInPrivateRoom(Long privateRoomId);
    Optional<PrivateChat> findPrivateChatsById(Long privateChatId);

    List<Long> findLastChatTimeOfPrivateRooms(List<Long> privateRoomIdList);
    boolean existsAlivePrivateRoomUser(Long userId, Long privateRoomId);
    Long findPrivateRoomIdOfChat(Long privateChatId);
    Map<Long, Long> findRecentChatOfRooms(List<Long> privateRoomUuidList);

    Map<Long, PrivateRoomResDto> getPrivateRoomList(Long userId, int page);
    List<PrivateChatResDto> getPrivateChatList(Long userId, Long privateRoomId, int page);
}
