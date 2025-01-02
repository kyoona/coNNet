package houseInception.connet.service;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.user.User;
import houseInception.connet.domain.group.Group;
import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.dto.DataListResDto;
import houseInception.connet.dto.group.*;
import houseInception.connet.event.publisher.GroupEventPublisher;
import houseInception.connet.exception.GroupException;
import houseInception.connet.externalServiceProvider.s3.S3ServiceProvider;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.service.util.CommonDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static houseInception.connet.response.status.BaseErrorCode.*;
import static houseInception.connet.service.util.FileUtil.getUniqueFileName;
import static houseInception.connet.service.util.FileUtil.isInValidFile;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final CommonDomainService domainService;
    private final S3ServiceProvider s3ServiceProvider;
    private final GroupEventPublisher groupEventPublisher;

    @Transactional
    public String addGroup(Long userId, GroupAddDto groupAddDto) {
        User user = domainService.findUser(userId);

        String groupProfileUrl = uploadImages(groupAddDto.getGroupProfile());

        Group group = Group.create(
                user,
                groupAddDto.getGroupName(),
                groupProfileUrl,
                groupAddDto.getGroupDescription(),
                groupAddDto.getUserLimit(),
                groupAddDto.getIsOpen());

        List<String> tagList = groupAddDto.getTags();
        if (!tagList.isEmpty() && !group.addTag(tagList)) {
            throw new GroupException(INVALID_GROUP_TAG);
        }

        groupRepository.save(group);

        groupEventPublisher.publishGroupAddEvent(group.getId());

        return group.getGroupUuid();
    }

    private String uploadImages(MultipartFile image){
        if (isInValidFile(image)) {
            return null;
        }

        String newFileName = getUniqueFileName(image.getOriginalFilename());
        return s3ServiceProvider.uploadImage(newFileName, image);
    }

    @Transactional
    public String enterGroup(Long userId, String groupUuid) {
        Group group = findGroupWithLock(groupUuid);
        checkOpenGroup(group);
        checkGroupLimit(group);

        User user = domainService.findUser(userId);
        checkUserInGroup(userId, groupUuid, false);

        group.addUser(user);

        return groupUuid;
    }

    @Transactional
    public Long exitGroup(Long userId, String groupUuid) {
        Group group = findGroup(groupUuid);
        GroupUser groupUser = groupRepository.findGroupUser(group.getId(), userId)
                .orElseThrow(() -> new GroupException(NOT_IN_GROUP));

        if(groupUser.isOwner()){
            throw new GroupException(OWNER_CAN_NOT_EXIT);
        }

        group.removeUser(groupUser);

        return groupUser.getId();
    }

    @Transactional
    public void addGroupUser(Long userId, String groupUuid) {
        User user = domainService.findUser(userId);

        Group group = findGroupWithLock(groupUuid);
        checkGroupLimit(group);

        group.addUser(user);
    }

    public GroupDetailResDto getGroupDetail(Long userId, String groupUuid) {
        checkUserInGroup(userId, groupUuid, true);

        GroupDetailResDto groupDetail = groupRepository.getGroupDetail(groupUuid);

        return groupDetail;
    }

    public List<GroupUserResDto> getGroupUserList(Long userId, String groupUuid) {
        checkUserInGroup(userId, groupUuid, true);

        List<GroupUserResDto> result = groupRepository.getGroupUserList(groupUuid);

        return result;
    }

    public DataListResDto<GroupResDto> getGroupList(Long userId, int page) {
        List<GroupResDto> groupList = groupRepository.getGroupList(userId, page);

        return new DataListResDto<>(page, groupList);
    }

    public DataListResDto<PublicGroupResDto> getPublicGroupList(Long userId, GroupFilter filter) {
        List<PublicGroupResDto> publicGroupList = groupRepository.getPublicGroupList(userId, filter);

        List<Long> groupIdList = publicGroupList.stream().map(PublicGroupResDto::getGroupId).toList();
        Map<Long, Long> groupUserCountMap = groupRepository.countOfGroupUsers(groupIdList);

        publicGroupList.forEach((res) -> res.setUserCount(groupUserCountMap.get(res.getGroupId())));

        return new DataListResDto<>(filter.getPage(), publicGroupList);
    }

    @Transactional
    public void exitGroupsOfUser(Long userId) {
        List<Group> groupOfOwner = groupRepository.findGroupListOfOwnerWithGroupUsers(userId);
        groupOfOwner.forEach((group) -> {
            group.removeAllUser();
            group.delete();
        });

        List<GroupUser> groupUsers = groupRepository.findGroupUserListOfNotOwnerWithGroup(userId);
        groupUsers.forEach((groupUser) -> {
            Group group = groupUser.getGroup();
            group.removeUser(groupUser);
        });
    }

    private Group findGroup(String groupUuid){
        return groupRepository.findByGroupUuidAndStatus(groupUuid, Status.ALIVE)
                .orElseThrow(() -> new GroupException(NO_SUCH_GROUP));
    }

    private Group findGroupWithLock(String groupUuid){
        return groupRepository.findByGroupUuidAndStatusWithLock(groupUuid, Status.ALIVE)
                .orElseThrow(() -> new GroupException(NO_SUCH_GROUP));
    }

    private void checkUserInGroup(Long userId, String groupUuid, boolean isInGroup){
        if (groupRepository.existUserInGroup(userId, groupUuid) != isInGroup) {
            if (isInGroup) {
                throw new GroupException(NOT_IN_GROUP);
            } else {
                throw new GroupException(ALREADY_IN_GROUP);
            }
        }
    }

    private void checkGroupLimit(Group group) {
        Long count = groupRepository.countOfGroupUsers(group.getId());
        if (count != null && count >= group.getUserLimit()) {
            throw new GroupException(GROUP_USER_LIMIT);
        }
    }

    private void checkOpenGroup(Group group){
        if(!group.isOpen()){
            throw new GroupException(PRIVATE_GROUP);
        }
    }
}
