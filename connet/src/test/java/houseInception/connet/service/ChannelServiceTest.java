package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.domain.channel.Channel;
import houseInception.connet.domain.group.Group;
import houseInception.connet.dto.channel.ChannelAddDto;
import houseInception.connet.repository.ChannelRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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
        ChannelAddDto channelAddDto = new ChannelAddDto("channel");
        Long resultId = channelService.addChannel(groupOwner.getId(), group.getGroupUuid(), channelAddDto);

        //then
        Channel channel = channelRepository.findById(resultId).orElseThrow();
        assertThat(channel.getGroupId()).isEqualTo(group.getId());
        assertThat(channel.getChannelName()).isEqualTo(channelAddDto.getChannelName());
    }
}