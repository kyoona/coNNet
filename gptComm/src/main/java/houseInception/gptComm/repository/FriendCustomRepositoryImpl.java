package houseInception.gptComm.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.gptComm.domain.Friend;
import houseInception.gptComm.domain.FriendStatus;
import houseInception.gptComm.domain.QFriend;
import houseInception.gptComm.domain.QUser;
import houseInception.gptComm.dto.DefaultUserResDto;
import houseInception.gptComm.dto.UserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static houseInception.gptComm.domain.FriendStatus.ACCEPT;
import static houseInception.gptComm.domain.FriendStatus.WAIT;
import static houseInception.gptComm.domain.QFriend.friend;
import static houseInception.gptComm.domain.QUser.user;

@RequiredArgsConstructor
@Repository
public class FriendCustomRepositoryImpl implements FriendCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public List<DefaultUserResDto> findFriendRequestList(Long userId) {
        return query.select(Projections.constructor(DefaultUserResDto.class,
                        user.id, user.userName, user.userProfile))
                .from(friend)
                .join(user).on(user.id.eq(friend.sender.id))
                .where(friend.receiver.id.eq(userId),
                        friend.acceptStatus.eq(WAIT))
                .fetch();
    }

    @Override
    public Optional<Friend> findFriendSenderOrReceiver(Long userId1, Long userId2) {
        Friend findFriend = query.selectFrom(friend)
                .where(
                        (friend.sender.id.eq(userId1).and(friend.receiver.id.eq(userId2)))
                                .or(friend.sender.id.eq(userId2).and(friend.receiver.id.eq(userId1)))
                )
                .fetchFirst();

        return Optional.ofNullable(findFriend);
    }

    @Override
    public List<Friend> findFriendListWithUser(Long userId) {
        QUser sender = new QUser("sender");
        QUser receiver = new QUser("receiver");

        return query.selectFrom(friend)
                .join(friend.sender, sender).fetchJoin()
                .join(friend.receiver, receiver).fetchJoin()
                .where((friend.receiver.id.eq(userId).or(friend.sender.id.eq(userId))
                        .and(friend.acceptStatus.eq(ACCEPT))))
                .fetch();
    }

    @Override
    public boolean existsFriend(Long userId, Long targetId) {
        Long count = query.select(friend.count())
                .from(friend)
                .where(
                        (friend.sender.id.eq(userId).and(friend.receiver.id.eq(targetId)))
                                .or(friend.sender.id.eq(targetId).and(friend.receiver.id.eq(userId)))
                )
                .fetchFirst();

        return count > 0;
    }
}
