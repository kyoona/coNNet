package houseInception.gptComm.repository;

import houseInception.gptComm.dto.UserResDto;

public interface UserCustomRepository {
    UserResDto findUserByEmailWithFriendRelation(Long userId, String email);
}
