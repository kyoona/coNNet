package houseInception.connet.service;

import houseInception.connet.domain.channel.Channel;
import houseInception.connet.dto.channel.ChannelDto;
import houseInception.connet.exception.ChannelException;
import houseInception.connet.exception.GroupException;
import houseInception.connet.repository.ChannelRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.service.util.DomainValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.connet.response.status.BaseErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChannelService {

    private final DomainValidatorUtil validator;
    private final ChannelRepository channelRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public Long addChannel(Long userId, String groupUuid, ChannelDto channelDto) {
        Long groupId = findGroupIdByUuid(groupUuid);
        checkGroupOwner(userId, groupId);

        Channel channel = Channel.create(groupId, channelDto.getChannelName());
        channelRepository.save(channel);

        return channel.getId();
    }

    @Transactional
    public Long updateChannel(Long userId, String groupUuid, Long channelId, ChannelDto channelDto) {
        Long groupId = findGroupIdByUuid(groupUuid);
        checkGroupOwner(userId, groupId);
        Channel channel = findChannel(channelId);

        channel.update(channelDto.getChannelName());

        return channelId;
    }

    @Transactional
    public Long deleteChannel(Long userId, String groupUuid, Long channelId) {
        Long groupId = findGroupIdByUuid(groupUuid);
        checkGroupOwner(userId, groupId);
        Channel channel = findChannel(channelId);

        channelRepository.delete(channel);

        return channelId;
    }

    private Channel findChannel(Long channelId){
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(NO_SUCH_CHANNEL));
    }

    private Long findGroupIdByUuid(String groupUuid){
        return groupRepository.findGroupIdByGroupUuid(groupUuid)
                .orElseThrow(() -> new GroupException(NO_SUCH_GROUP));
    }

    private void checkGroupOwner(Long userId, Long groupId){
        if(!groupRepository.existGroupOwner(userId, groupId)){
            throw new ChannelException(ONLY_GROUP_OWNER);
        }
    }
}
