package houseInception.gptComm.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.gptComm.domain.QFriend;
import houseInception.gptComm.domain.QUser;
import houseInception.gptComm.dto.UserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static houseInception.gptComm.domain.QFriend.friend;
import static houseInception.gptComm.domain.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public UserResDto findUserByEmailWithFriendRelation(Long userId, String email) {
        QUser targetUser = new QUser("target");
        return query.select(Projections.constructor(UserResDto.class,
                        targetUser.id, targetUser.userName, targetUser.userProfile,
                        friend.sender.id, friend.acceptStatus))
                .from(targetUser)
                .leftJoin(friend).on((friend.sender.id.eq(userId).and(friend.receiver.id.eq(targetUser.id)))
                        .or(friend.sender.id.eq(targetUser.id).and(friend.receiver.id.eq(userId))))
                .where(targetUser.email.eq(email))
                .fetchOne();
    }
}
