package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.domain.channel.Channel;
import houseInception.connet.domain.channel.ChannelTap;
import houseInception.connet.domain.group.Group;
import houseInception.connet.dto.channel.ChannelDto;
import houseInception.connet.dto.channel.ChannelResDto;
import houseInception.connet.dto.channel.TapDto;
import houseInception.connet.dto.channel.TapResDto;
import houseInception.connet.exception.ChannelException;
import houseInception.connet.exception.GroupException;
import houseInception.connet.repository.ChannelRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class ChannelServiceTest {

    @Autowired
    ChannelService channelService;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    EntityManager em;

    User user1;
    User user2;
    User user3;
    User groupOwner;
    Group group;

    @BeforeEach
    void beforeEach(){
        groupOwner = User.create("user4", null, null, null);
        user1 = User.create("user1", null, null, null);
        user2 = User.create("user2", null, null, null);
        user3 = User.create("user3", null, null, null);
        em.persist(groupOwner);
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);

        group = Group.create(groupOwner, "group", null, null, 10, true);
        em.persist(group);
    }

    @AfterEach
    void afterEach(){
        userRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    void addChannel() {
        //when
        ChannelDto channelDto = new ChannelDto("channel");
        Long resultId = channelService.addChannel(groupOwner.getId(), group.getGroupUuid(), channelDto);

        //then
        Channel channel = channelRepository.findById(resultId).orElseThrow();
        assertThat(channel.getGroupId()).isEqualTo(group.getId());
        assertThat(channel.getChannelName()).isEqualTo(channelDto.getChannelName());
    }

    @Test
    void addChannel_그룹멤버o_방장x() {
        //given
        group.addUser(user1);
        em.flush();

        //when
        ChannelDto channelDto = new ChannelDto("channel");
        assertThatThrownBy(() -> channelService.addChannel(user1.getId(), group.getGroupUuid(), channelDto))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void updateChannel() {
        //given
        Channel channel = Channel.create(group.getId(), "channel");
        em.persist(channel);

        //when
        ChannelDto channelDto = new ChannelDto("new channel");
        Long resultId = channelService.updateChannel(groupOwner.getId(), group.getGroupUuid(), channel.getId(), channelDto);

        //then
        Channel testChannel = channelRepository.findById(resultId).orElseThrow();
        assertThat(testChannel.getChannelName()).isEqualTo(channelDto.getChannelName());
    }

    @Test
    void updateChannel_그룹멤버o_방장x() {
        //given
        group.addUser(user1);
        em.flush();

        Channel channel = Channel.create(group.getId(), "channel");
        em.persist(channel);

        //when
        ChannelDto channelDto = new ChannelDto("new channel");
        assertThatThrownBy(() -> channelService.updateChannel(user1.getId(), group.getGroupUuid(), channel.getId(), channelDto))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void deleteChannel() {
        //given
        Channel channel = Channel.create(group.getId(), "channel");
        em.persist(channel);

        //when
        Long resultId = channelService.deleteChannel(groupOwner.getId(), group.getGroupUuid(), channel.getId());

        //then
        assertThat(channelRepository.findById(resultId)).isEmpty();
    }

    @Test
    void deleteChannel_그룹멤버o_방장x() {
        //given
        group.addUser(user1);
        em.flush();

        Channel channel = Channel.create(group.getId(), "channel");
        em.persist(channel);

        //when
        assertThatThrownBy(() -> channelService.deleteChannel(user1.getId(), group.getGroupUuid(), channel.getId()))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void addTap() {
        //given
        Channel channel = Channel.create(group.getId(), "channel");
        em.persist(channel);

        //when
        TapDto tapDto = new TapDto("tap");
        Long resultId = channelService.addTap(groupOwner.getId(), group.getGroupUuid(), channel.getId(), tapDto);

        //then
        assertThat(channel.getTapList()).hasSize(1);
        assertThat(channel.getTapList().get(0).getTapName()).isEqualTo(tapDto.getTapName());
    }

    @Test
    void updateTap() {
        //given
        Channel channel = Channel.create(group.getId(), "channel");
        ChannelTap tap = channel.addTap("tap");
        em.persist(channel);

        //when
        TapDto tapDto = new TapDto("newTap");
        Long resultId = channelService.updateTap(groupOwner.getId(), group.getGroupUuid(), tap.getId(), tapDto);

        //then
        assertThat(tap.getTapName()).isEqualTo(tapDto.getTapName());
    }

    @Test
    void deleteTap() {
        //given
        Channel channel = Channel.create(group.getId(), "channel");
        ChannelTap tap = channel.addTap("tap");
        em.persist(channel);

        //when
        Long resultId = channelService.deleteTap(groupOwner.getId(), group.getGroupUuid(), tap.getId());

        //then
        assertThat(channelRepository.findChannelTap(resultId)).isEmpty();
    }

    @Test
    void deleteTap_권한x() {
        //given
        Channel channel = Channel.create(group.getId(), "channel");
        ChannelTap tap = channel.addTap("tap");
        em.persist(channel);

        //when
        assertThatThrownBy(() -> channelService.deleteTap(user1.getId(), group.getGroupUuid(), tap.getId()))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void getChannelTapList() {
        //given
        Channel channel1 = Channel.create(group.getId(), "channel1");
        ChannelTap tap1 = channel1.addTap("tap1");
        ChannelTap tap2 = channel1.addTap("tap2");
        em.persist(channel1);

        Channel channel2 = Channel.create(group.getId(), "channel2");
        em.persist(channel2);

        //when
        List<ChannelResDto> result = channelService.getChannelTapList(groupOwner.getId(), group.getGroupUuid());

        //then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(ChannelResDto::getChannelId)
                .containsExactly(channel1.getId(), channel2.getId());

        assertThat(result.get(0).getTaps())
                .extracting(TapResDto::getTapId)
                .containsExactly(tap1.getId(), tap2.getId());

        assertThat(result.get(1).getTaps()).hasSize(0);
    }

    @Test
    void addDefaultChannelTap() {
        //when
        Long channelId = channelService.addDefaultChannelTap(group.getId());

        //then
        Channel channel = channelRepository.findById(channelId).get();
        assertThat(channel.getChannelName()).isEqualTo("채널1");
        assertThat(channel.getTapList()).hasSize(1);
    }
}