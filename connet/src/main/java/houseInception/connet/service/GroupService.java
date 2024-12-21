package houseInception.connet.service;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.User;
import houseInception.connet.domain.group.Group;
import houseInception.connet.dto.group.GroupAddDto;
import houseInception.connet.dto.group.GroupUserResDto;
import houseInception.connet.exception.GroupException;
import houseInception.connet.externalServiceProvider.s3.S3ServiceProvider;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.service.util.DomainValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static houseInception.connet.response.status.BaseErrorCode.*;
import static houseInception.connet.service.util.FileUtil.getUniqueFileName;
import static houseInception.connet.service.util.FileUtil.isInValidFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final DomainValidatorUtil validator;
    private final S3ServiceProvider s3ServiceProvider;

    @Value("${aws.s3.imageUrlPrefix}")
    private String s3UrlPrefix;

    @Transactional
    public String addGroup(Long userId, GroupAddDto groupAddDto) {
        User user = validator.findUser(userId);

        String groupProfileUrl = uploadImages(groupAddDto.getGroupProfile());

        Group group = Group.create(
                user,
                groupAddDto.getGroupName(),
                groupProfileUrl,
                groupAddDto.getGroupDescription(),
                groupAddDto.getUserLimit(),
                groupAddDto.isOpen());

        List<String> tagList = groupAddDto.getTags();
        if (!tagList.isEmpty() && !group.addTag(tagList)) {
            throw new GroupException(INVALID_GROUP_TAG);
        }

        groupRepository.save(group);

        return group.getGroupUuid();
    }

    private String uploadImages(MultipartFile image){
        if (isInValidFile(image)) {
            return null;
        }

        String newFileName = getUniqueFileName(image.getOriginalFilename());
        s3ServiceProvider.uploadImage(newFileName, image);

        return s3UrlPrefix + newFileName;
    }

    @Transactional
    public String enterGroup(Long userId, String groupUuid) {
        Group group = findGroupWithLock(groupUuid);
        checkOpenGroup(group);
        checkGroupLimit(group);

        User user = validator.findUser(userId);
        checkUserInGroup(userId, groupUuid, false);

        group.addUser(user);

        return groupUuid;
    }

    private void checkGroupLimit(Group group) {
        Long count = groupRepository.countOfGroupUsers(group.getId());
        if (count != null && count >= group.getUserLimit()) {
            throw new GroupException(GROUP_USER_LIMIT);
        }
    }

    @Transactional
    public void addGroupUser(Long userId, String groupUuid) {
        User user = validator.findUser(userId);

        Group group = findGroupWithLock(groupUuid);
        checkGroupLimit(group);

        group.addUser(user);
    }

    public List<GroupUserResDto> getGroupUserList(Long userId, String groupUuid) {
        checkUserInGroup(userId, groupUuid, true);

        List<GroupUserResDto> result = groupRepository.getGroupUserList(groupUuid);

        return result;
    }

    private void checkOpenGroup(Group group){
        if(!group.isOpen()){
            throw new GroupException(PRIVATE_GROUP);
        }
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

    private Group findGroup(String groupUuid){
        return groupRepository.findByGroupUuidAndStatus(groupUuid, Status.ALIVE)
                .orElseThrow(() -> new GroupException(NO_SUCH_GROUP));
    }

    private Group findGroupWithLock(String groupUuid){
        return groupRepository.findByGroupUuidAndStatusWithLock(groupUuid, Status.ALIVE)
                .orElseThrow(() -> new GroupException(NO_SUCH_GROUP));
    }
}
