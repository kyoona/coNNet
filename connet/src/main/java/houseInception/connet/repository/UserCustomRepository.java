package houseInception.connet.repository;


import houseInception.connet.dto.DefaultUserResDto;

public interface UserCustomRepository {

    DefaultUserResDto getUserProfile(Long userId);
}
