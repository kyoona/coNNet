package houseInception.gptComm.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.gptComm.domain.Status;
import houseInception.gptComm.domain.chatRoom.Chat;
import houseInception.gptComm.domain.chatRoom.QChat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static houseInception.gptComm.domain.Status.ALIVE;
import static houseInception.gptComm.domain.chatRoom.QChat.chat;

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
}
