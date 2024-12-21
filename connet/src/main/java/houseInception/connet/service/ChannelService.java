package houseInception.connet.service;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.User;
import houseInception.connet.domain.channel.Channel;
import houseInception.connet.dto.channel.ChannelAddDto;
import houseInception.connet.exception.GroupException;
import houseInception.connet.repository.ChannelRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.response.status.BaseErrorCode;
import houseInception.connet.service.util.DomainValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChannelService {

    private final DomainValidatorUtil validator;
    private final ChannelRepository channelRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public Long addChannel(Long userId, String groupUuid, ChannelAddDto channelAddDto) {
        User user = validator.findUser(userId);
        Long groupId = findGroupIdByUuid(groupUuid);

        Channel channel = Channel.create(groupId, channelAddDto.getChannelName());
        channelRepository.save(channel);

        return channel.getId();
    }

    private Long findGroupIdByUuid(String groupUuid){
        Long groupId = groupRepository.findIdByGroupUuidAndStatus(groupUuid, Status.ALIVE);
        if(groupId == null){
            throw new GroupException(BaseErrorCode.NO_SUCH_GROUP);
        }

        return groupId;
    }
}
