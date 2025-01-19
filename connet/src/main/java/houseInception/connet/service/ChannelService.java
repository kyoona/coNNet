package houseInception.connet.service;

import houseInception.connet.domain.channel.Channel;
import houseInception.connet.domain.channel.ChannelTap;
import houseInception.connet.domain.group.Group;
import houseInception.connet.dto.channel.ChannelDto;
import houseInception.connet.dto.channel.ChannelResDto;
import houseInception.connet.dto.channel.TapDto;
import houseInception.connet.exception.ChannelException;
import houseInception.connet.exception.GroupException;
import houseInception.connet.repository.ChannelRepository;
import houseInception.connet.repository.ChatReadLogRepository;
import houseInception.connet.repository.GroupChatRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.repository.dto.ChannelTapDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static houseInception.connet.response.status.BaseErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final GroupRepository groupRepository;
    private final ChatReadLogRepository chatReadLogRepository;
    private final GroupChatRepository groupChatRepository;
    private final EntityManager em;

    @Transactional
    public Long addChannel(Long userId, String groupUuid, ChannelDto channelDto) {
        Long groupId = findGroupIdByUuid(groupUuid);
        checkGroupOwner(userId, groupUuid);

        Channel channel = Channel.create(groupId, channelDto.getChannelName());
        channelRepository.save(channel);

        return channel.getId();
    }

    @Transactional
    public Long updateChannel(Long userId, String groupUuid, Long channelId, ChannelDto channelDto) {
        checkGroupOwner(userId, groupUuid);
        Channel channel = findChannel(channelId);

        channel.update(channelDto.getChannelName());

        return channelId;
    }

    @Transactional
    public Long deleteChannel(Long userId, String groupUuid, Long channelId) {
        checkGroupOwner(userId, groupUuid);
        Channel channel = findChannel(channelId);

        channelRepository.delete(channel);

        return channelId;
    }

    @Transactional
    public Long addTap(Long userId, String groupUuid, Long channelId, TapDto tapDto) {
        checkGroupOwner(userId, groupUuid);
        Channel channel = findChannel(channelId);

        ChannelTap tap = channel.addTap(tapDto.getTapName());
        em.flush();

        return tap.getId();
    }

    @Transactional
    public Long updateTap(Long userId, String groupUuid, Long tapId, TapDto tapDto) {
        checkGroupOwner(userId, groupUuid);
        ChannelTap tap = findTapWithChannel(tapId);
        Channel channel = tap.getChannel();

        channel.updateTap(tap, tapDto.getTapName());

        return tapId;
    }

    @Transactional
    public Long deleteTap(Long userId, String groupUuid, Long tapId) {
        checkGroupOwner(userId, groupUuid);

        Long deleteCount = channelRepository.deleteTapById(tapId);
        if(deleteCount == 0){
            throw new ChannelException(NO_SUCH_TAP);
        }

        return tapId;
    }

    public List<ChannelResDto> getChannelTapList(Long userId, String groupUuid) {
        checkUserInGroup(userId, groupUuid);

        List<ChannelTapDto> channelTapList = channelRepository.getChannelTapListOfGroup(groupUuid);

        List<Long> tapIdList = channelTapList.stream()
                .filter((tap) -> tap.getTapId() != null)
                .map(ChannelTapDto::getTapId)
                .toList();
        Map<Long, Long> recentReadLogOfTaps = chatReadLogRepository.findRecentReadLogOfTaps(tapIdList);
        Map<Long, Long> recentChatOfTaps = groupChatRepository.findRecentGroupChatOfTaps(tapIdList);


        Map<Long, List<ChannelTapDto>> groupedTap = channelTapList.stream()
                .collect(Collectors.groupingBy(ChannelTapDto::getChannelId));

        return groupedTap.values().stream()
                .map((dtoList) -> new ChannelResDto(dtoList, recentChatOfTaps, recentReadLogOfTaps))
                .toList();
    }

    @Transactional
    public Long addDefaultChannelTap(Long groupId) {
        Channel channel = Channel.create(groupId, "채널1");
        channel.addTap("채팅 탭");
        channelRepository.save(channel);

        return channel.getId();
    }

    private Channel findChannel(Long channelId){
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(NO_SUCH_CHANNEL));
    }

    private ChannelTap findTapWithChannel(Long tapId){
        return channelRepository.findChannelTapWithChannel(tapId)
                .orElseThrow(() -> new ChannelException(NO_SUCH_TAP));
    }

    private Long findGroupIdByUuid(String groupUuid){
        return groupRepository.findGroupIdByGroupUuid(groupUuid)
                .orElseThrow(() -> new GroupException(NO_SUCH_GROUP));
    }

    private void checkGroupOwner(Long userId, String groupUuid){
        if(!groupRepository.existGroupOwner(userId, groupUuid)){
            throw new GroupException(ONLY_GROUP_OWNER);
        }
    }

    private void checkUserInGroup(Long userId, String groupUuid){
        if (!groupRepository.existUserInGroup(userId, groupUuid)) {
            throw new GroupException(NOT_IN_GROUP);
        }
    }
}
