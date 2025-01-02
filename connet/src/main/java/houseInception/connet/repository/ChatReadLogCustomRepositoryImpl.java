package houseInception.connet.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.ChatRoomType;
import houseInception.connet.domain.group.QGroup;
import houseInception.connet.domain.privateRoom.QPrivateRoom;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static houseInception.connet.domain.QChatReadLog.chatReadLog;
import static houseInception.connet.domain.privateRoom.QPrivateRoom.privateRoom;

@RequiredArgsConstructor
public class ChatReadLogCustomRepositoryImpl implements ChatReadLogCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public Map<Long, Long> findRecentReadLogOfTaps(List<Long> tapIdList) {
        List<Tuple> fetchData = query
                .select(chatReadLog.tapId, chatReadLog.chatId.max())
                .from(chatReadLog)
                .where(
                        chatReadLog.tapId.in(tapIdList),
                        chatReadLog.type.eq(ChatRoomType.GROUP)
                )
                .groupBy(chatReadLog.tapId)
                .fetch();

        return fetchData.stream()
                .collect(Collectors.toMap(
                        (tuple) -> tuple.get(chatReadLog.tapId),
                        (tuple) -> tuple.get(chatReadLog.chatId.max())
                ));
    }

    @Override
    public Map<Long, Long> findRecentReadLogOfPrivateRooms(List<String> privateRoomUuidList) {
        List<Tuple> fetchedData = query
                .select(privateRoom.id, chatReadLog.chatId.max())
                .from(chatReadLog)
                .innerJoin(privateRoom).on(privateRoom.privateRoomUuid.eq(chatReadLog.privateRoomUuid))
                .where(
                        chatReadLog.privateRoomUuid.in(privateRoomUuidList),
                        chatReadLog.type.eq(ChatRoomType.PRIVATE)
                )
                .groupBy(privateRoom.id)
                .fetch();

        return fetchedData.stream()
                .collect(Collectors.toMap(
                        (tuple) -> tuple.get(privateRoom.id),
                        (tuple) -> tuple.get(chatReadLog.chatId.max())
                ));
    }
}
