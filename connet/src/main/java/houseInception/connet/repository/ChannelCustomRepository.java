package houseInception.connet.repository;

import houseInception.connet.domain.channel.ChannelTap;

import java.util.Optional;

public interface ChannelCustomRepository {

    Long deleteTapById(Long tapId);

    Optional<ChannelTap> findChannelTapWithChannel(Long tapId);
}
