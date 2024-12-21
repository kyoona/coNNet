package houseInception.connet.service;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.channel.Channel;
import houseInception.connet.dto.channel.ChannelAddDto;
import houseInception.connet.exception.ChannelException;
import houseInception.connet.exception.GroupException;
import houseInception.connet.repository.ChannelRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.service.util.DomainValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.connet.response.status.BaseErrorCode.NO_SUCH_GROUP;
import static houseInception.connet.response.status.BaseErrorCode.ONLY_GROUP_OWNER;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChannelService {

    private final DomainValidatorUtil validator;
    private final ChannelRepository channelRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public Long addChannel(Long userId, String groupUuid, ChannelAddDto channelAddDto) {
        Long groupId = findGroupIdByUuid(groupUuid);
        checkGroupOwner(userId, groupId);

        Channel channel = Channel.create(groupId, channelAddDto.getChannelName());
        channelRepository.save(channel);

        return channel.getId();
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
