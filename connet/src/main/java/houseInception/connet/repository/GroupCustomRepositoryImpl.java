package houseInception.connet.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.QUser;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.group.QGroup;
import houseInception.connet.domain.group.QGroupUser;
import houseInception.connet.dto.group.GroupUserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static houseInception.connet.domain.QUser.user;
import static houseInception.connet.domain.group.QGroup.group;
import static houseInception.connet.domain.group.QGroupUser.groupUser;

@RequiredArgsConstructor
@Repository
public class GroupCustomRepositoryImpl implements GroupCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public boolean existUserInGroup(Long userId, String groupUuid) {
        Long count = query
                .select(groupUser.count())
                .from(groupUser)
                .innerJoin(groupUser.group, group)
                .where(group.groupUuid.eq(groupUuid),
                        groupUser.user.id.eq(userId),
                        groupUser.status.eq(Status.ALIVE))
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public List<GroupUserResDto> getGroupUserList(String groupUuid) {
        return query
                .select(Projections.constructor(
                        GroupUserResDto.class,
                        user.id,
                        user.userName,
                        user.userProfile,
                        user.isActive,
                        groupUser.isOwner
                ))
                .from(groupUser)
                .innerJoin(groupUser.group, group)
                .innerJoin(groupUser.user, user)
                .where(groupUser.status.eq(Status.ALIVE),
                        group.groupUuid.eq(groupUuid))
                .orderBy(
                        groupUser.isOwner.desc(),
                        user.userName.asc())
                .fetch();
    }
}
