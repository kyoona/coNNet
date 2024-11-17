package houseInception.connet.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.privateRoom.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static houseInception.connet.domain.privateRoom.QPrivateChat.privateChat;
import static houseInception.connet.domain.privateRoom.QPrivateRoom.privateRoom;
import static houseInception.connet.domain.privateRoom.QPrivateRoomUser.privateRoomUser;

@RequiredArgsConstructor
@Repository
public class PrivateRoomCustomRepositoryImpl implements PrivateRoomCustomRepository{

    private final JPAQueryFactory query;


    @Override
    public Optional<PrivateRoom> findPrivateRoomWithUser(String privateRoomUuid) {
        PrivateRoom findPrivateRoom = query.selectFrom(privateRoom)
                .join(privateRoom.privateRoomUsers).fetchJoin()
                .where(privateRoom.privateRoomUuid.eq(privateRoomUuid))
                .fetchOne();

        return Optional.ofNullable(findPrivateRoom);
    }

    @Override
    public Optional<PrivateRoomUser> findPrivateRoomUser(Long privateRoomId, Long userId) {
        PrivateRoomUser findPrivateRoomUser = query.selectFrom(privateRoomUser)
                .where(privateRoomUser.privateRoom.id.eq(privateRoomId),
                        privateRoomUser.user.id.eq(userId))
                .fetchOne();

        return Optional.ofNullable(findPrivateRoomUser);
    }

    @Override
    public List<PrivateChat> findPrivateChatsInPrivateRoom(Long privateRoomId) {
        return query.selectFrom(privateChat)
                .where(privateChat.privateRoom.id.eq(privateRoomId))
                .fetch();
    }
}
