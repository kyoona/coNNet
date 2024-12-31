package houseInception.connet.event.handler;

import houseInception.connet.domain.User;
import houseInception.connet.dto.group.GroupAddDto;
import houseInception.connet.repository.ChannelRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.repository.dto.ChannelTapDto;
import houseInception.connet.service.GroupService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EventChannelHandlerTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    GroupService groupService;
    @Autowired
    EntityManager em;

    User user1;

    @BeforeEach
    void beforeEach(){
        user1 = User.create("user1", null, null, null);
        userRepository.save(user1);
    }

    @AfterEach
    void afterEach(){
        channelRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addDefaultChannelTap() {
        //given
        GroupAddDto groupAddDto = new GroupAddDto("group", null, null, List.of(), 3, false);
        String groupUuid = groupService.addGroup(user1.getId(), groupAddDto);

        //when
        List<ChannelTapDto> channelTaps = channelRepository.getChannelTapListOfGroup(groupUuid);

        //then
        assertThat(channelTaps).hasSize(1);
        assertThat(channelTaps)
                .extracting(ChannelTapDto::getChannelName)
                .contains("채널1");
    }
}