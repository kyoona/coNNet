package houseInception.connet.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.dto.group.GroupFilter;
import houseInception.connet.dto.group.GroupResDto;
import houseInception.connet.dto.group.GroupUserResDto;
import houseInception.connet.dto.group.PublicGroupResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static houseInception.connet.domain.QGroupChat.groupChat;
import static houseInception.connet.domain.QUser.user;
import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.group.QGroup.group;
import static houseInception.connet.domain.group.QGroupTag.groupTag;
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
                        groupUser.status.eq(ALIVE))
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
                        groupUser.status.eq(ALIVE))
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
                        groupUser.status.eq(ALIVE))
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
                        groupUser.status.eq(ALIVE))
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public Long countOfGroupUsers(Long groupId) {
        return query
                .select(groupUser.count())
                .from(groupUser)
                .where(groupUser.group.id.eq(groupId),
                        groupUser.status.eq(ALIVE))
                .fetchOne();
    }

    @Override
    public Map<Long, Long> countOfGroupUsers(List<Long> groupId) {
        List<Tuple> groupUserCountList = query
                .select(groupUser.group.id, groupUser.id.count())
                .from(group)
                .leftJoin(group.groupUserList, groupUser)
                .where(
                        groupUser.status.eq(ALIVE),
                        groupUser.group.id.in(groupId)
                )
                .groupBy(groupUser.group.id)
                .fetch();

        return groupUserCountList.stream()
                .collect(Collectors.toMap(
                        (tuple) -> tuple.get(groupUser.group.id),
                        (tuple) -> tuple.get(groupUser.id.count())
                ));
    }

    @Override
    public Optional<Long> findGroupIdByGroupUuid(String groupUuid) {
        Long id = query
                .select(group.id)
                .from(group)
                .where(group.groupUuid.eq(groupUuid),
                        group.status.eq(ALIVE))
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
                        groupUser.status.eq(ALIVE))
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
                .where(groupUser.status.eq(ALIVE),
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
                        groupUser.status.eq(ALIVE),
                        group.status.eq(ALIVE))
                .orderBy(group.createdAt.desc())
                .offset((page - 1) * 30)
                .limit(31)
                .fetch();
    }

    @Override
    public List<PublicGroupResDto> getPublicGroupList(Long userId, GroupFilter filter) {
        List<Long> groupIdList = query
                .select(group.id, group.createdAt)
                .from(group)
                .leftJoin(group.groupTagList, groupTag)
                .where(
                        group.isOpen.isTrue(),
                        group.status.eq(ALIVE),
                        groupNameOrTagContains(filter.getFilter())
                )
                .distinct()
                .orderBy(group.createdAt.desc())
                .offset((filter.getPage() - 1) * 30)
                .limit(31)
                .fetch()
                .stream()
                .map((tuple) -> tuple.get(group.id))
                .toList();

        return query
                .select(Projections.constructor(
                        PublicGroupResDto.class,
                        group.id,
                        group.groupUuid,
                        group.groupName,
                        group.groupProfile,
                        group.groupDescription,
                        Expressions.stringTemplate("GROUP_CONCAT({0})", groupTag.tagName),
                        group.userLimit,
                        JPAExpressions
                                .select(groupChat.createdAt.max())
                                .from(groupChat)
                                .where(groupChat.groupId.eq(group.id)),
                        JPAExpressions
                                .select(groupUser.id)
                                .from(groupUser)
                                .where(
                                        groupUser.group.id.eq(group.id),
                                        groupUser.user.id.eq(userId),
                                        groupUser.status.eq(ALIVE))
                ))
                .from(group)
                .leftJoin(group.groupTagList, groupTag)
                .where(
                        group.id.in(groupIdList),
                        group.isOpen.isTrue(),
                        group.status.eq(ALIVE)
                )
                .groupBy(group.id)
                .orderBy(group.createdAt.desc())
                .fetch();
    }

    private BooleanExpression groupNameOrTagContains(String str){
        return str == null
                ? null
                : group.groupName.contains(str).or(groupTag.tagName.contains(str));
    }
}
