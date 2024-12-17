package houseInception.connet.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.ChatRoomType;
import houseInception.connet.domain.privateRoom.*;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.PrivateChatResDto;
import houseInception.connet.dto.PrivateRoomResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static houseInception.connet.domain.QChatEmoji.chatEmoji;
import static houseInception.connet.domain.QUser.user;
import static houseInception.connet.domain.Status.ALIVE;
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
    public Optional<PrivateRoom> findPrivateRoomByUsers(Long userId, Long targetId) {
        QPrivateRoomUser subPrivateUser = new QPrivateRoomUser("subPrivateUser");
        PrivateRoom fetchedPrivateRoom = query
                .select(privateRoom)
                .from(privateRoomUser)
                .innerJoin(subPrivateUser).on(privateRoomUser.privateRoom.id.eq(subPrivateUser.privateRoom.id))
                .innerJoin(privateRoomUser.privateRoom, privateRoom)
                .where(privateRoomUser.user.id.eq(userId),
                        subPrivateUser.user.id.eq(targetId),
                        privateRoom.status.eq(ALIVE))
                .fetchOne();

        return Optional.ofNullable(fetchedPrivateRoom);
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

    @Override
    public Optional<PrivateChat> findPrivateChatsById(Long privateChatId) {
        PrivateChat findPrivateChat = query.selectFrom(privateChat)
                .where(privateChat.id.eq(privateChatId))
                .fetchOne();

        return Optional.ofNullable(findPrivateChat);
    }

    @Override
    public boolean existsAlivePrivateRoomUser(Long userId, Long privateRoomId) {
        Long count = query.select(privateRoomUser.count())
                .from(privateRoomUser)
                .where(privateRoomUser.privateRoom.id.eq(privateRoomId),
                        privateRoomUser.user.id.eq(userId),
                        privateRoomUser.status.eq(ALIVE))
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public boolean existsAlivePrivateRoomUser(Long userId, String privateRoomUuid) {
        Long count = query.select(privateRoomUser.count())
                .from(privateRoomUser)
                .innerJoin(privateRoom).on(privateRoom.id.eq(privateRoomUser.privateRoom.id))
                .where(privateRoom.privateRoomUuid.eq(privateRoomUuid),
                        privateRoomUser.user.id.eq(userId),
                        privateRoomUser.status.eq(ALIVE))
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public Long getPrivateRoomIdOfChat(Long privateChatId) {
        return query
                .select(privateChat.privateRoom.id)
                .from(privateChat)
                .where(privateChat.id.eq(privateChatId),
                        privateChat.status.eq(ALIVE))
                .fetchOne();
    }

    public Optional<PrivateRoomUser> findTargetRoomUserWithUserInChatRoom(Long userId, Long privateRoomId) {
        PrivateRoomUser targetPrivateRoomUser = query
                .selectFrom(privateRoomUser)
                .innerJoin(privateRoom).on(privateRoom.id.eq(privateRoomUser.privateRoom.id))
                .innerJoin(privateRoomUser.user, user).fetchJoin()
                .where(user.id.ne(userId),
                        privateRoom.id.eq(privateRoomId),
                        privateRoom.status.eq(ALIVE))
                .fetchOne();

        return Optional.ofNullable(targetPrivateRoomUser);
    }

    @Override
    public List<PrivateChatResDto> getPrivateChatList(Long userId, Long privateRoomId, int page) {
        QPrivateRoomUser subPrivateRoomUser = new QPrivateRoomUser("subPrivateRoomUser");

        return query.select(Projections.constructor(PrivateChatResDto.class,
                        privateChat.id,
                        privateChat.message,
                        privateChat.image,
                        privateChat.writerRole,
                        Projections.constructor(DefaultUserResDto.class,
                                user.id, user.userName, user.userProfile),
                        privateChat.createdAt,
                        ExpressionUtils.as(
                                JPAExpressions.select(Expressions.stringTemplate("GROUP_CONCAT({0})", chatEmoji.emojiType))
                                        .from(chatEmoji)
                                        .where(chatEmoji.chatId.eq(privateChat.id),
                                                chatEmoji.chatRoomType.eq(ChatRoomType.PRIVATE)),
                                "emojiAggStr")
                ))
                .from(privateChat)
                .leftJoin(privateRoomUser).on(privateRoomUser.id.eq(privateChat.writer.id))
                .leftJoin(user).on(user.id.eq(privateRoomUser.user.id))
                .where(privateChat.privateRoom.id.eq(privateRoomId),
                        privateChat.createdAt.goe(
                                JPAExpressions.select(subPrivateRoomUser.participationTime)
                                        .from(subPrivateRoomUser)
                                        .where(subPrivateRoomUser.user.id.eq(userId))
                        ))
                .offset((page - 1) * 30)
                .limit(31)
                .orderBy(privateChat.createdAt.desc())
                .fetch();
    }

    public Map<Long, PrivateRoomResDto> getPrivateRoomList(Long userId, int page) {
        QPrivateRoomUser subPrivateRoomUser = new QPrivateRoomUser("subPrivateRoomUser");

        List<PrivateRoomResDto> privateRoomList = query.select(Projections.constructor(PrivateRoomResDto.class,
                        privateRoom.id, privateRoom.privateRoomUuid, user.id, user.userName, user.userProfile, user.isActive))
                .from(privateRoom)
                .rightJoin(privateRoomUser).on(privateRoomUser.privateRoom.id.eq(privateRoom.id))
                .innerJoin(user).on(user.id.eq(privateRoomUser.user.id))
                .where(privateRoom.id.in(
                                JPAExpressions.select(subPrivateRoomUser.privateRoom.id)
                                        .from(subPrivateRoomUser)
                                        .where(subPrivateRoomUser.user.id.eq(userId),
                                                subPrivateRoomUser.status.eq(ALIVE))
                        ), privateRoomUser.user.id.ne(userId),
                        privateRoom.status.eq(ALIVE))
                .offset((page - 1) * 30)
                .limit(31)
                .fetch();

        return privateRoomList.stream().collect(Collectors.toMap(
                privateRoomResDto -> privateRoomResDto.getChatRoomId(),
                privateRoomResDto -> privateRoomResDto
        ));
    }

    @Override
    public List<Long> getLastChatTimeOfPrivateRooms(List<Long> privateRoomIdList) {
        return query.select(privateChat.privateRoom.id)
                .from(privateChat)
                .where(privateChat.privateRoom.id.in(privateRoomIdList))
                .groupBy(privateChat.privateRoom.id)
                .orderBy(privateChat.createdAt.max().desc())
                .fetch();
    }
}
