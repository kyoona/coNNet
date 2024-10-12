package houseInception.gptComm.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.gptComm.domain.QUser;
import houseInception.gptComm.domain.Status;
import houseInception.gptComm.domain.chatRoom.*;
import houseInception.gptComm.dto.GptChatRoomChatResDto;
import houseInception.gptComm.dto.GptChatRoomListResDto;
import houseInception.gptComm.dto.UserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static houseInception.gptComm.domain.QUser.user;
import static houseInception.gptComm.domain.Status.ALIVE;
import static houseInception.gptComm.domain.chatRoom.ChatRoomType.GPT;
import static houseInception.gptComm.domain.chatRoom.QChat.chat;
import static houseInception.gptComm.domain.chatRoom.QChatRoom.chatRoom;
import static houseInception.gptComm.domain.chatRoom.QChatRoomUser.chatRoomUser;

@RequiredArgsConstructor
@Repository
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public List<Chat> getChatListOfChatRoom(Long chatRoomId) {
        return query
                .selectFrom(chat)
                .where(chat.chatRoom.id.eq(chatRoomId),
                        chat.status.eq(ALIVE))
                .orderBy(chat.createdAt.asc())
                .fetch();
    }

    @Override
    public boolean existsChatRoomUser(Long chatRoomId, Long userId, Status status) {
        Long userCount = query
                .select(chatRoomUser.count())
                .from(chatRoomUser)
                .where(chatRoomUser.chatRoom.id.eq(chatRoomId),
                        chatRoomUser.user.id.eq(userId),
                        chatRoomUser.status.eq(status))
                .fetchOne();

        return userCount > 0;
    }

    @Override
    public List<GptChatRoomListResDto> getGptChatRoomListByUserId(Long userId, int page) {
        return query
                .select(Projections.constructor(GptChatRoomListResDto.class,
                        chatRoom.chatRoomUuid, chatRoom.title, chatRoom.createdAt))
                .from(chatRoom)
                .innerJoin(chatRoomUser).on(chatRoomUser.chatRoom.id.eq(chatRoom.id))
                .innerJoin(user).on(user.id.eq(chatRoomUser.user.id))
                .where(user.id.eq(userId),
                        chatRoom.chatRoomType.eq(GPT),
                        chatRoomUser.status.eq(ALIVE),
                        chatRoom.status.eq(ALIVE))
                .orderBy(chatRoom.createdAt.desc())
                .offset((page - 1) * 30)
                .limit(31)
                .fetch();
    }

    @Override
    public List<GptChatRoomChatResDto> getGptChatRoomChatList(Long chatRoomId, int page) {
        return query
                .select(Projections.constructor(GptChatRoomChatResDto.class,
                        chat.id, chat.content, chat.writerRole, user.id, user.userName, user.userProfile, chat.createdAt))
                .from(chat)
                .innerJoin(chatRoom).on(chatRoom.id.eq(chat.chatRoom.id))
                .leftJoin(chatRoomUser).on(chatRoomUser.chatRoom.id.eq(chatRoom.id))
                .leftJoin(user).on(user.id.eq(chatRoomUser.user.id))
                .where(chatRoom.id.eq(chatRoomId),
                        chatRoom.chatRoomType.eq(GPT),
                        chat.status.eq(ALIVE))
                .orderBy(chat.createdAt.desc())
                .offset((page - 1) * 30)
                .limit(31)
                .fetch();
    }
}
