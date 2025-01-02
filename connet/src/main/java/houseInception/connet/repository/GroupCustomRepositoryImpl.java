package houseInception.connet.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.group.Group;
import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.domain.group.QGroup;
import houseInception.connet.domain.group.QGroupUser;
import houseInception.connet.dto.group.*;
import houseInception.connet.dto.user.CommonGroupOfUserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static houseInception.connet.domain.QGroupChat.groupChat;
import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.group.QGroup.group;
import static houseInception.connet.domain.group.QGroupTag.groupTag;
import static houseInception.connet.domain.group.QGroupUser.groupUser;
import static houseInception.connet.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class GroupCustomRepositoryImpl implements GroupCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public Optional<Group> findGroupWithGroupUsers(Long groupId) {
        Group fetchedGroup = query
                .selectFrom(group)
                .leftJoin(group.groupUserList, groupUser).fetchJoin()
                .where(
                        group.id.eq(groupId),
                        group.status.eq(ALIVE)
                )
                .fetchOne();

        return Optional.ofNullable(fetchedGroup);
    }

    @Override
    public Optional<Group> findGroupWithTags(String groupUuid) {
        Group fetchedGroup = query
                .selectFrom(group)
                .leftJoin(group.groupTagList, groupTag).fetchJoin()
                .where(
                        group.groupUuid.eq(groupUuid),
                        group.status.eq(ALIVE)
                )
                .fetchOne();

        return Optional.ofNullable(fetchedGroup);
    }

    @Override
    public List<Group> findGroupListOfOwnerWithGroupUsers(Long userId) {
        QGroupUser subGroupUser = new QGroupUser("subGroupUser");
        return query
                .selectFrom(group)
                .leftJoin(group.groupUserList, groupUser).fetchJoin()
                .where(
                        group.id.in(
                                JPAExpressions.select(subGroupUser.group.id)
                                        .from(subGroupUser)
                                        .where(
                                                subGroupUser.user.id.eq(userId),
                                                subGroupUser.isOwner.isTrue(),
                                                subGroupUser.status.eq(ALIVE)
                                        )
                        ),
                        group.status.eq(ALIVE)
                )
                .fetch();
    }

    @Override
    public Optional<GroupUser> findGroupUser(Long groupId, Long userId) {
        GroupUser fetchedGroupUser = query
                .selectFrom(groupUser)
                .where(
                        groupUser.group.id.eq(groupId),
                        groupUser.user.id.eq(userId),
                        groupUser.status.eq(ALIVE)
                )
                .fetchOne();

        return Optional.ofNullable(fetchedGroupUser);
    }

    @Override
    public Optional<GroupUser> findGroupUser(String groupUuid, Long userId) {
        GroupUser fetchedGroupUser = query
                .selectFrom(groupUser)
                .innerJoin(groupUser.group, group)
                .where(
                        group.groupUuid.eq(groupUuid),
                        groupUser.user.id.eq(userId),
                        groupUser.status.eq(ALIVE)
                )
                .fetchOne();

        return Optional.ofNullable(fetchedGroupUser);
    }

    @Override
    public List<GroupUser> findGroupUserListOfNotOwnerWithGroup(Long userId) {
        return query
                .selectFrom(groupUser)
                .innerJoin(groupUser.group, group)
                .where(
                        groupUser.user.id.eq(userId),
                        groupUser.isOwner.isFalse(),
                        groupUser.status.eq(ALIVE)
                )
                .fetch();
    }

    @Override
    public boolean existUserInGroup(Long userId, String groupUuid) {
        Long count = query
                .select(groupUser.count())
                .from(groupUser)
                .innerJoin(groupUser.group, group)
                .where(
                        group.groupUuid.eq(groupUuid),
                        groupUser.user.id.eq(userId),
                        groupUser.status.eq(ALIVE)
                )
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public boolean existUserInGroup(Long userId, Long groupId) {
        Long count = query
                .select(groupUser.count())
                .from(groupUser)
                .where(
                        groupUser.user.id.eq(userId),
                        groupUser.group.id.eq(groupId),
                        groupUser.status.eq(ALIVE)
                )
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public boolean existGroupOwner(Long userId, String groupUuid) {
        Long count = query
                .select(groupUser.count())
                .from(groupUser)
                .innerJoin(groupUser.group, group)
                .where(
                        group.groupUuid.eq(groupUuid),
                        groupUser.user.id.eq(userId),
                        groupUser.isOwner.isTrue(),
                        groupUser.status.eq(ALIVE)
                )
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public Long countOfGroupUsers(Long groupId) {
        return query
                .select(groupUser.count())
                .from(groupUser)
                .where(
                        groupUser.group.id.eq(groupId),
                        groupUser.status.eq(ALIVE)
                )
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
                .where(
                        group.groupUuid.eq(groupUuid),
                        group.status.eq(ALIVE)
                )
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
                .where(
                        group.groupUuid.eq(groupUuid),
                        user.id.ne(userId),
                        groupUser.status.eq(ALIVE)
                )
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
                .where(
                        groupUser.status.eq(ALIVE),
                        group.groupUuid.eq(groupUuid)
                )
                .orderBy(
                        groupUser.isOwner.desc(),
                        user.userName.asc()
                )
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
                .where(
                        groupUser.user.id.eq(userId),
                        groupUser.status.eq(ALIVE),
                        group.status.eq(ALIVE)
                )
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

    @Override
    public GroupDetailResDto getGroupDetail(String groupUuid) {
        return query
                .select(Projections.constructor(
                        GroupDetailResDto.class,
                        group.groupUuid,
                        group.groupName,
                        group.groupProfile,
                        group.groupDescription,
                        Expressions.stringTemplate("GROUP_CONCAT({0})", groupTag.tagName),
                        group.userLimit,
                        group.isOpen
                ))
                .from(group)
                .leftJoin(group.groupTagList, groupTag)
                .groupBy(group.id)
                .where(group.groupUuid.eq(groupUuid))
                .fetchOne();
    }

    @Override
    public List<CommonGroupOfUserResDto> getCommonGroupList(Long userId, Long targetId) {
        QGroupUser subGroupUser = new QGroupUser("subGroupUser");

        return query
                .select(Projections.constructor(
                        CommonGroupOfUserResDto.class,
                        group.groupUuid,
                        group.groupName,
                        group.groupProfile
                ))
                .from(groupUser)
                .innerJoin(groupUser.group, group)
                .where(
                        groupUser.user.id.eq(targetId),
                        groupUser.status.eq(ALIVE),
                        group.id.in(
                                JPAExpressions.select(subGroupUser.group.id)
                                        .from(subGroupUser)
                                        .where(
                                                subGroupUser.user.id.eq(userId),
                                                subGroupUser.status.eq(ALIVE)
                                        )
                        )
                )
                .orderBy(groupUser.createdAt.desc())
                .fetch();
    }

    private BooleanExpression groupNameOrTagContains(String str){
        return str == null
                ? null
                : group.groupName.contains(str).or(groupTag.tagName.contains(str));
    }
}
