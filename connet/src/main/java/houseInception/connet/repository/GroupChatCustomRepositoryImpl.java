package houseInception.connet.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.ChatRoomType;
import houseInception.connet.domain.ChatterRole;
import houseInception.connet.domain.Status;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.groupChat.GroupChatResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static houseInception.connet.domain.QChatEmoji.chatEmoji;
import static houseInception.connet.domain.QGroupChat.groupChat;
import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.group.QGroupUser.groupUser;
import static houseInception.connet.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class GroupChatCustomRepositoryImpl implements GroupChatCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public Optional<Long> findGroupIdOfChat(Long chatId) {
        Long groupId = query
                .select(groupChat.groupId)
                .from(groupChat)
                .where(groupChat.id.eq(chatId))
                .fetchOne();

        return Optional.ofNullable(groupId);
    }

    @Override
    public Map<Long, Long> findRecentGroupChatOfTaps(List<Long> tapList) {
        List<Tuple> fetchedData = query
                .select(groupChat.tapId, groupChat.id.max())
                .from(groupChat)
                .where(groupChat.tapId.in(tapList))
                .groupBy(groupChat.tapId)
                .fetch();

        return fetchedData.stream()
                .collect(Collectors.toMap(
                        (tuple) -> tuple.get(groupChat.tapId),
                        (tuple) -> tuple.get(groupChat.id.max())
                ));
    }

    @Override
    public Optional<String> findLastGptChatOfTap(Long tapId) {
        String chat = query
                .select(groupChat.message)
                .from(groupChat)
                .where(
                        groupChat.tapId.eq(tapId),
                        groupChat.writerRole.eq(ChatterRole.GPT),
                        groupChat.status.eq(ALIVE)
                )
                .orderBy(groupChat.createdAt.desc())
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(chat);
    }

    @Override
    public List<GroupChatResDto> getChatList(Long tapId, int page) {
        return query
                .select(Projections.constructor(
                        GroupChatResDto.class,
                        groupChat.id,
                        groupChat.message,
                        groupChat.image,
                        groupChat.writerRole,
                        Projections.constructor(
                                DefaultUserResDto.class,
                                user.id,
                                user.userName,
                                user.userProfile
                        ),
                        groupChat.createdAt,
                        ExpressionUtils.as(
                                JPAExpressions.select(Expressions.stringTemplate("GROUP_CONCAT({0})", chatEmoji.emojiType))
                                        .from(chatEmoji)
                                        .where(chatEmoji.chatId.eq(groupChat.id),
                                                chatEmoji.chatRoomType.eq(ChatRoomType.GROUP)),
                                "emojiAggStr")
                ))
                .from(groupChat)
                .leftJoin(groupChat.writer, groupUser)
                .leftJoin(groupUser.user, user)
                .where(groupChat.tapId.eq(tapId))
                .orderBy(groupChat.createdAt.desc())
                .offset((page - 1) * 30)
                .limit(31)
                .fetch();
    }
}
