package houseInception.connet.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.ChatRoomType;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.groupChat.GroupChatResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static houseInception.connet.domain.QChatEmoji.chatEmoji;
import static houseInception.connet.domain.QGroupChat.groupChat;
import static houseInception.connet.domain.QUser.user;
import static houseInception.connet.domain.group.QGroupUser.groupUser;

@RequiredArgsConstructor
@Repository
public class GroupChatCustomRepositoryImpl implements GroupChatCustomRepository{

    private final JPAQueryFactory query;

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
                                                chatEmoji.chatRoomType.eq(ChatRoomType.MULTI)),
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
