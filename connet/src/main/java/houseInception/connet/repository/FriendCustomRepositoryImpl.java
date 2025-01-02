package houseInception.connet.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.dto.ActiveUserResDto;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.friend.FriendFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static houseInception.connet.domain.FriendStatus.ACCEPT;
import static houseInception.connet.domain.FriendStatus.WAIT;
import static houseInception.connet.domain.QFriend.friend;
import static houseInception.connet.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class FriendCustomRepositoryImpl implements FriendCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public List<DefaultUserResDto> getFriendWaitList(Long userId) {
        return query.select(Projections.constructor(DefaultUserResDto.class,
                        user.id, user.userName, user.userProfile))
                .from(friend)
                .innerJoin(friend.sender, user)
                .where(
                        friend.receiver.id.eq(userId),
                        friend.acceptStatus.eq(WAIT)
                )
                .fetch();
    }

    @Override
    public List<DefaultUserResDto> getFriendRequestList(Long userId) {
        return query.select(Projections.constructor(DefaultUserResDto.class,
                        user.id, user.userName, user.userProfile))
                .from(friend)
                .innerJoin(friend.receiver, user)
                .where(
                        friend.sender.id.eq(userId),
                        friend.acceptStatus.eq(WAIT)
                )
                .fetch();
    }

    @Override
    public void deleteAllFriendsOfUser(Long userId) {
        query.delete(friend)
                .where(
                        friend.receiver.id.eq(userId)
                                .or(friend.sender.id.eq(userId))
                )
                .execute();
    }

    @Override
    public List<ActiveUserResDto> getFriendList(Long userId, FriendFilterDto filterDto) {
        return query.select(Projections.constructor(ActiveUserResDto.class,
                        user.id, user.userName, user.userProfile, user.isActive))
                .from(friend)
                .innerJoin(friend.receiver, user)
                .where(
                        friend.sender.id.eq(userId),
                        friend.acceptStatus.eq(ACCEPT),
                        userNameContains(filterDto.getUserName())
                )
                .fetch();
    }

    private BooleanExpression userNameContains(String userName) {
        return (userName != null && !userName.isEmpty())
                ? user.userName.contains(userName)
                : null;
    }

    @Override
    public boolean existsFriendRequest(Long userId, Long targetId) {
        Long count = query.select(friend.count())
                .from(friend)
                .where(
                        ((friend.sender.id.eq(userId).and(friend.receiver.id.eq(targetId)))
                                .or(friend.sender.id.eq(targetId).and(friend.receiver.id.eq(userId)))),
                        friend.acceptStatus.eq(WAIT)
                )
                .fetchFirst();

        return count > 0;
    }
}
