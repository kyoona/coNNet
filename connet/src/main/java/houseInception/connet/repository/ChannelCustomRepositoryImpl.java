package houseInception.connet.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.channel.ChannelTap;
import houseInception.connet.domain.channel.QChannel;
import houseInception.connet.domain.channel.QChannelTap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static houseInception.connet.domain.channel.QChannel.channel;
import static houseInception.connet.domain.channel.QChannelTap.channelTap;

@RequiredArgsConstructor
@Repository
public class ChannelCustomRepositoryImpl implements ChannelCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public Long deleteTapById(Long tapId) {
        return query
                .delete(channelTap)
                .where(channelTap.id.eq(tapId))
                .execute();
    }

    @Override
    public Optional<ChannelTap> findChannelTapWithChannel(Long tapId) {
        ChannelTap tap = query
                .selectFrom(channelTap)
                .innerJoin(channelTap.channel, channel).fetchJoin()
                .where(channelTap.id.eq(tapId))
                .fetchOne();

        return Optional.ofNullable(tap);
    }
}
