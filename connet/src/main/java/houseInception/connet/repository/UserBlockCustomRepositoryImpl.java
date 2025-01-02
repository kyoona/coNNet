package houseInception.connet.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.dto.DefaultUserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static houseInception.connet.domain.QUserBlock.userBlock;
import static houseInception.connet.domain.UserBlockType.REQUEST;
import static houseInception.connet.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserBlockCustomRepositoryImpl implements UserBlockCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public void deleteAllUserBlockOfUser(Long userId) {
        query
                .delete(userBlock)
                .where(
                        userBlock.user.id.eq(userId)
                                .or(userBlock.target.id.eq(userId))
                )
                .execute();
    }

    @Override
    public List<DefaultUserResDto> getBlockUserList(Long userId) {
        return query
                .select(Projections.constructor(
                DefaultUserResDto.class,
                        user.id,
                        user.userName,
                        user.userProfile))
                .from(userBlock)
                .innerJoin(userBlock.target, user)
                .where(
                        userBlock.user.id.eq(userId),
                        userBlock.blockType.eq(REQUEST)
                )
                .orderBy(user.userName.asc())
                .fetch();
    }
}
