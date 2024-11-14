package houseInception.connet.repository;

import houseInception.connet.dto.DefaultUserResDto;

import java.util.List;

public interface UserBlockCustomRepository {

    List<DefaultUserResDto> getBlockUserList(Long userId);
}
