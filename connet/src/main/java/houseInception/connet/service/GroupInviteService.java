package houseInception.connet.service;

import houseInception.connet.domain.GroupInvite;
import houseInception.connet.domain.User;
import houseInception.connet.dto.group_invite.GroupInviteDto;
import houseInception.connet.exception.GroupException;
import houseInception.connet.exception.GroupInviteException;
import houseInception.connet.repository.GroupInviteRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.service.util.DomainValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.connet.response.status.BaseErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupInviteService {

    private final DomainValidatorUtil validator;
    private final GroupInviteRepository groupInviteRepository;
    private final GroupRepository groupRepository;

    public Long inviteGroup(Long userId, String groupUuid, GroupInviteDto inviteDto) {
        User user = validator.findUser(userId);
        User targetUser = validator.findUser(inviteDto.getTargetId());

        checkUserInGroup(userId, groupUuid);
        checkUserAlreadyInGroup(targetUser.getId(), groupUuid);
        checkGroupInviteOfUser(targetUser.getId(), groupUuid, false);

        GroupInvite groupInvite = GroupInvite.create(groupUuid, user, targetUser);
        groupInviteRepository.save(groupInvite);

        return groupInvite.getId();
    }

    public void checkUserInGroup(Long userId, String groupUuid){
        if (!groupRepository.existUserInGroup(userId, groupUuid)) {
            throw new GroupException(NOT_IN_GROUP);
        }
    }

    private void checkUserAlreadyInGroup(Long userId, String groupUuid){
        if (groupRepository.existUserInGroup(userId, groupUuid)) {
            throw new GroupException(ALREADY_IN_GROUP);
        }
    }

    private void checkGroupInviteOfUser(Long userId, String groupUuid, boolean isInvite){
        if(groupInviteRepository.existsByGroupUuidAndInvitee(groupUuid, userId) != isInvite){
            if(isInvite){
                throw new GroupInviteException(NO_SUCH_GROUP_INVITE);
            }else{
                throw new GroupInviteException(ALREADY_HAS_GROUP_INVITE);
            }
        }
    }
}
