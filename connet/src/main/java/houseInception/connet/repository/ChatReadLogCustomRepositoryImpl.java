package houseInception.connet.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.ChatRoomType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static houseInception.connet.domain.QChatReadLog.chatReadLog;

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
}
