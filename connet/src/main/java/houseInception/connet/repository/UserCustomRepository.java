package houseInception.connet.repository;

import houseInception.connet.dto.user.UserProfileResDto;

public interface UserCustomRepository {

    UserProfileResDto getUserProfile(Long userId);
}
