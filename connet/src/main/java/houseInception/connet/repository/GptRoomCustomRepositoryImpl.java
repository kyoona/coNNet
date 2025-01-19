package houseInception.connet.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.gptRoom.*;
import houseInception.connet.dto.GptRoom.GptRoomChatResDto;
import houseInception.connet.dto.GptRoom.GptRoomListResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.gptRoom.QGptRoom.gptRoom;
import static houseInception.connet.domain.gptRoom.QGptRoomChat.gptRoomChat;
import static houseInception.connet.domain.gptRoom.QGptRoomUser.gptRoomUser;
import static houseInception.connet.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class GptRoomCustomRepositoryImpl implements GptRoomCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public List<GptRoomChat> getChatListOfGptRoom(Long gptRoomId) {
        return query
                .selectFrom(gptRoomChat)
                .where(
                        gptRoomChat.gptRoom.id.eq(gptRoomId),
                        gptRoomChat.status.eq(ALIVE)
                )
                .orderBy(gptRoomChat.createdAt.asc())
                .fetch();
    }

    @Override
    public Optional<String> getLastGptChat(Long gptRoomId) {
        String chat = query
                .select(gptRoomChat.content)
                .from(gptRoomChat)
                .where(
                        gptRoomChat.gptRoom.id.eq(gptRoomId),
                        gptRoomChat.writerRole.eq(ChatterRole.GPT),
                        gptRoomChat.status.eq(ALIVE)
                )
                .orderBy(gptRoomChat.createdAt.desc())
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(chat);
    }

    @Override
    public boolean existsGptRoomUser(Long gptRoomId, Long userId, Status status) {
        Long userCount = query
                .select(gptRoomUser.count())
                .from(gptRoomUser)
                .where(
                        gptRoomUser.gptRoom.id.eq(gptRoomId),
                        gptRoomUser.user.id.eq(userId),
                        gptRoomUser.status.eq(status)
                )
                .fetchOne();

        return userCount > 0;
    }

    @Override
    public List<GptRoomListResDto> getGptRoomListByUserId(Long userId, int page) {
        return query
                .select(Projections.constructor(GptRoomListResDto.class,
                        gptRoom.gptRoomUuid, gptRoom.title, gptRoom.createdAt))
                .from(gptRoom)
                .innerJoin(gptRoom.gptRoomUsers, gptRoomUser)
                .innerJoin(gptRoomUser.user, user)
                .where(
                        user.id.eq(userId),
                        gptRoomUser.status.eq(ALIVE),
                        gptRoom.status.eq(ALIVE)
                )
                .orderBy(gptRoom.createdAt.desc())
                .offset((page - 1) * 30)
                .limit(31)
                .fetch();
    }

    @Override
    public List<GptRoomChatResDto> getGptChatRoomChatList(Long gptRoomId, int page) {
        return query
                .select(Projections.constructor(
                        GptRoomChatResDto.class,
                        gptRoomChat.id,
                        gptRoomChat.content,
                        gptRoomChat.writerRole,
                        user.id, user.userName,
                        user.userProfile,
                        gptRoomChat.createdAt))
                .from(gptRoomChat)
                .innerJoin(gptRoomChat.gptRoom, gptRoom)
                .leftJoin(gptRoom.gptRoomUsers, gptRoomUser)
                .leftJoin(gptRoomUser.user, user)
                .where(
                        gptRoom.id.eq(gptRoomId),
                        gptRoomChat.status.eq(ALIVE)
                )
                .orderBy(gptRoomChat.createdAt.desc())
                .offset((page - 1) * 30)
                .limit(31)
                .fetch();
    }
}
