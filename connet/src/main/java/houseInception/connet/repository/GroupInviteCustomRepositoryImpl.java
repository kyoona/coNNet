package houseInception.connet.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.group_invite.GroupInviteResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static houseInception.connet.domain.QGroupInvite.groupInvite;
import static houseInception.connet.domain.group.QGroup.group;
import static houseInception.connet.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class GroupInviteCustomRepositoryImpl implements GroupInviteCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public List<GroupInviteResDto> getGroupInviteListOfUser(Long userId) {
        return query
                .select(Projections.constructor(
                        GroupInviteResDto.class,
                        group.groupUuid,
                        group.groupName,
                        group.groupProfile,
                        Projections.constructor(
                                DefaultUserResDto.class,
                                user.id,
                                user.userName,
                                user.userProfile
                        )
                ))
                .from(groupInvite)
                .innerJoin(group).on(group.groupUuid.eq(groupInvite.groupUuid))
                .innerJoin(groupInvite.inviter, user)
                .where(groupInvite.invitee.id.eq(userId))
                .orderBy(groupInvite.createdAt.desc())
                .fetch();
    }
}
