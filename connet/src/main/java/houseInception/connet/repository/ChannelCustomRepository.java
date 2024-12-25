package houseInception.connet.repository;

import houseInception.connet.domain.channel.ChannelTap;
import houseInception.connet.repository.dto.ChannelTapDto;

import java.util.List;
import java.util.Optional;

public interface ChannelCustomRepository {

    Long deleteTapById(Long tapId);

    Optional<ChannelTap> findChannelTapWithChannel(Long tapId);
    Optional<ChannelTap> findChannelTap(Long tapId);
    boolean existsTap(Long tapId);

    List<ChannelTapDto> getChannelTapListOfGroup(String groupUuid);
}
