package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.domain.channel.Channel;
import houseInception.connet.domain.group.Group;
import houseInception.connet.dto.channel.ChannelDto;
import houseInception.connet.exception.ChannelException;
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
                .isInstanceOf(ChannelException.class);
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
                .isInstanceOf(ChannelException.class);
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
                .isInstanceOf(ChannelException.class);
    }
}