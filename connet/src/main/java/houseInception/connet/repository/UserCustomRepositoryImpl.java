package houseInception.connet.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.user.UserProfileResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public UserProfileResDto getUserProfile(Long userId) {
        return query
                .select(Projections.constructor(
                        UserProfileResDto.class,
                        user.id,
                        user.userName,
                        user.userProfile,
                        user.userDescription
                ))
                .from(user)
                .where(
                        user.id.eq(userId),
                        user.status.eq(ALIVE)
                )
                .fetchOne();
    }
}
