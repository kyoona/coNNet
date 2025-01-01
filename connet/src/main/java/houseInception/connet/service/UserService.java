package houseInception.connet.service;

import houseInception.connet.domain.user.Setting;
import houseInception.connet.domain.user.User;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.user.CommonGroupOfUserResDto;
import houseInception.connet.dto.user.SettingUpdateDto;
import houseInception.connet.dto.user.UserProfileUpdateDto;
import houseInception.connet.event.publisher.UserEventPublisher;
import houseInception.connet.externalServiceProvider.s3.S3ServiceProvider;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.service.util.CommonDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static houseInception.connet.service.util.FileUtil.getUniqueFileName;
import static houseInception.connet.service.util.FileUtil.isInValidFile;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CommonDomainService domainService;
    private final S3ServiceProvider s3ServiceProvider;
    private final UserEventPublisher userEventPublisher;

    public DefaultUserResDto getSelfProfile(Long userId) {
        return userRepository.getUserProfile(userId);
    }

    public DefaultUserResDto getUserProfile(Long userId) {
        User user = domainService.findUser(userId);

        return new DefaultUserResDto(user);
    }

    @Transactional
    public Long updateProfile(Long userId, UserProfileUpdateDto profileDto) {
        User user = domainService.findUser(userId);

        String imageUrl = uploadImages(profileDto.getUserProfile());
        imageUrl = (imageUrl == null)
                ? user.getUserProfile()
                : imageUrl;

        user.update(profileDto.getUserName(), imageUrl, profileDto.getUserDescription());

        return userId;
    }

    private String uploadImages(MultipartFile image){
        if (isInValidFile(image)) {
            return null;
        }

        String newFileName = getUniqueFileName(image.getOriginalFilename());
        return s3ServiceProvider.uploadImage(newFileName, image);
    }

    @Transactional
    public Long deleteUser(Long userId) {
        User user = domainService.findUser(userId);
        user.delete();

        userEventPublisher.publishUserDeleteEvent(userId);

        return userId;
    }

    @Transactional
    public Long updateSetting(Long userId, SettingUpdateDto settingDto) {
        User user = domainService.findUser(userId);
        user.setSetting(new Setting(settingDto.isAlarm()));

        return userId;
    }

    public Setting getSetting(Long userId) {
        User user = domainService.findUser(userId);

        return user.getSetting();
    }

    public List<CommonGroupOfUserResDto> getCommonGroupList(Long userId, Long targetId) {
        return groupRepository.getCommonGroupList(userId, targetId);
    }

    @Transactional
    public void setUserActive(Long userId){
        User user = domainService.findUser(userId);
        user.setActive();
    }

    @Transactional
    public void setUserInActive(Long userId){
        User user = domainService.findUser(userId);
        user.setInActive();
    }
}
