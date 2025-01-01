package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.user.UserProfileUpdateDto;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.service.util.CommonDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static houseInception.connet.service.util.FileUtil.getUniqueFileName;
import static houseInception.connet.service.util.FileUtil.isInValidFile;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CommonDomainService domainService;

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
