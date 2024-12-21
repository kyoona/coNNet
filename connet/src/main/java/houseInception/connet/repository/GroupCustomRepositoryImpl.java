package houseInception.connet.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.group.QGroup;
import houseInception.connet.domain.group.QGroupUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
