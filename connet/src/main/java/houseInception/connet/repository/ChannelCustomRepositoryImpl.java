package houseInception.connet.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.channel.ChannelTap;
import houseInception.connet.domain.channel.QChannel;
import houseInception.connet.domain.channel.QChannelTap;
import houseInception.connet.domain.group.QGroup;
import houseInception.connet.repository.dto.ChannelTapDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static houseInception.connet.domain.channel.QChannel.channel;
import static houseInception.connet.domain.channel.QChannelTap.channelTap;
import static houseInception.connet.domain.group.QGroup.group;

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

    @Override
    public Optional<ChannelTap> findChannelTap(Long tapId) {
        ChannelTap tap = query
                .selectFrom(channelTap)
                .where(channelTap.id.eq(tapId))
                .fetchOne();

        return Optional.ofNullable(tap);
    }

    @Override
    public List<ChannelTapDto> getChannelTapListOfGroup(String groupUuid) {
        return query
                .select(Projections.constructor(
                        ChannelTapDto.class,
                        channel.id,
                        channel.channelName,
                        channelTap.id,
                        channelTap.tapName
                ))
                .from(channel)
                .innerJoin(group).on(group.id.eq(channel.groupId))
                .leftJoin(channelTap).on(channelTap.channel.id.eq(channel.id))
                .where(group.groupUuid.eq(groupUuid))
                .orderBy(channel.createdAt.asc(), channelTap.createdAt.asc())
                .fetch();
    }
}
