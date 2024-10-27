package houseInception.gptComm.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.gptComm.domain.QFriend;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static houseInception.gptComm.domain.QFriend.friend;

@RequiredArgsConstructor
@Repository
public class FriendCustomRepositoryImpl implements FriendCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public boolean existsFriend(Long userId, Long targetId) {
        Long count = query.select(friend.count())
                .from(friend)
                .where(
                        (friend.sender.id.eq(userId).and(friend.recipient.id.eq(targetId)))
                                .or(friend.sender.id.eq(targetId).and(friend.recipient.id.eq(userId)))
                )
                .fetchFirst();

        return count > 0;
    }
}
