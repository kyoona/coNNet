package houseInception.connet.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.dto.group.GroupResDto;
import houseInception.connet.dto.group.GroupUserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static houseInception.connet.domain.QUser.user;
import static houseInception.connet.domain.group.QGroup.group;
import static houseInception.connet.domain.group.QGroupUser.groupUser;

@RequiredArgsConstructor
@Repository
public class GroupCustomRepositoryImpl implements GroupCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public Optional<GroupUser> findGroupUser(Long groupId, Long userId) {
        GroupUser fetchedGroupUser = query
                .selectFrom(groupUser)
                .where(groupUser.group.id.eq(groupId),
                        groupUser.user.id.eq(userId),
                        groupUser.status.eq(Status.ALIVE))
                .fetchOne();

        return Optional.ofNullable(fetchedGroupUser);
    }

    @Override
    public Optional<GroupUser> findGroupUser(String groupUuid, Long userId) {
        GroupUser fetchedGroupUser = query
                .selectFrom(groupUser)
                .innerJoin(groupUser.group, group)
                .where(group.groupUuid.eq(groupUuid),
                        groupUser.user.id.eq(userId),
                        groupUser.status.eq(Status.ALIVE))
                .fetchOne();

        return Optional.ofNullable(fetchedGroupUser);
    }

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
    public boolean existGroupOwner(Long userId, String groupUuid) {
        Long count = query
                .select(groupUser.count())
                .from(groupUser)
                .innerJoin(groupUser.group, group)
                .where(group.groupUuid.eq(groupUuid),
                        groupUser.user.id.eq(userId),
                        groupUser.isOwner.isTrue(),
                        groupUser.status.eq(Status.ALIVE))
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public Long countOfGroupUsers(Long groupId) {
        return query
                .select(groupUser.count())
                .from(groupUser)
                .where(groupUser.group.id.eq(groupId),
                        groupUser.status.eq(Status.ALIVE))
                .fetchOne();
    }

    @Override
    public Optional<Long> findGroupIdByGroupUuid(String groupUuid) {
        Long id = query
                .select(group.id)
                .from(group)
                .where(group.groupUuid.eq(groupUuid),
                        group.status.eq(Status.ALIVE))
                .fetchOne();

        return Optional.ofNullable(id);
    }

    @Override
    public List<Long> findUserIdsOfGroupExceptUser(String groupUuid, Long userId) {
        return query
                .select(user.id)
                .from(groupUser)
                .innerJoin(groupUser.user, user)
                .innerJoin(groupUser.group, group)
                .where(group.groupUuid.eq(groupUuid),
                        user.id.ne(userId),
                        groupUser.status.eq(Status.ALIVE))
                .fetch();
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

    @Override
    public List<GroupResDto> getGroupList(Long userId, int page) {
        return query
                .select(Projections.constructor(
                        GroupResDto.class,
                        group.groupUuid,
                        group.groupName,
                        group.groupProfile
                ))
                .from(groupUser)
                .innerJoin(groupUser.group, group)
                .where(groupUser.user.id.eq(userId),
                        groupUser.status.eq(Status.ALIVE),
                        group.status.eq(Status.ALIVE))
                .orderBy(group.createdAt.desc())
                .offset((page - 1) * 30)
                .limit(31)
                .fetch();
    }
}
