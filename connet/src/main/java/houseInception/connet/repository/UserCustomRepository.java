package houseInception.connet.repository;

import houseInception.connet.dto.UserResDto;

public interface UserCustomRepository {

    UserResDto findUserByEmailWithFriendRelation(Long userId, String email);
}
