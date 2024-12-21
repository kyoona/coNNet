package houseInception.connet.repository;

import houseInception.connet.domain.channel.ChannelTap;

import java.util.Optional;

public interface ChannelCustomRepository {

    Optional<ChannelTap> findChannelTapWithChannel(Long tapId);
}
