package houseInception.connet.repository;

import houseInception.connet.domain.privateRoom.PrivateChat;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;

import java.util.List;
import java.util.Optional;

public interface PrivateRoomCustomRepository {

    Optional<PrivateRoom> findPrivateRoomWithUser(String privateRoomUuid);
    Optional<PrivateRoomUser> findPrivateRoomUser(Long privateRoomId, Long userId);
    List<PrivateChat> findPrivateChatsInPrivateRoom(Long privateRoomId);

    boolean existsAlivePrivateRoomUser(Long userId, Long privateRoomId);
    Long getPrivateRoomIdOfChat(Long privateChatId);
}
