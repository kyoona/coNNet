package houseInception.connet.service;

import houseInception.connet.domain.GroupChat;
import houseInception.connet.domain.User;
import houseInception.connet.domain.channel.Channel;
import houseInception.connet.domain.channel.ChannelTap;
import houseInception.connet.domain.group.Group;
import houseInception.connet.dto.groupChat.GroupChatAddDto;
import houseInception.connet.dto.groupChat.GroupChatAddResDto;
import houseInception.connet.dto.groupChat.GroupGptChatAddDto;
import houseInception.connet.dto.groupChat.GroupGptChatAddResDto;
import houseInception.connet.exception.GroupChatException;
import houseInception.connet.exception.GroupException;
import houseInception.connet.repository.ChannelRepository;
import houseInception.connet.repository.GroupChatRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@Transactional
@SpringBootTest
class GroupChatServiceTest {

    @Autowired
    GroupChatService groupChatService;
    @Autowired
    GroupChatRepository groupChatRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    EntityManager em;
    
    User groupOwner;
    User groupUser;
    Group group;
    Channel groupChannel;
    ChannelTap groupChannelTap;

    @BeforeEach
    void beforeEach(){
        groupOwner = User.create("groupOwner", null, null, null);
        groupUser = User.create("groupUser", null, null, null);
        em.persist(groupOwner);
        em.persist(groupUser);
        
        group = Group.create(groupOwner, "group", null, null, 10, true);
        group.addUser(groupUser);
        em.persist(group);
        
        groupChannel = Channel.create(group.getId(), "channel");
        groupChannelTap = groupChannel.addTap("tap");
        em.persist(groupChannel);
    }

    @AfterEach
    void afterEach(){
        channelRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addChat() {
        //when
        GroupChatAddDto chatAddDto = new GroupChatAddDto("message", null, groupChannelTap.getId());
        GroupChatAddResDto result = groupChatService.addChat(groupUser.getId(), group.getGroupUuid(), chatAddDto);

        //then
        GroupChat groupChat = groupChatRepository.findById(result.getChatId()).orElseThrow();
        assertThat(groupChat.getTapId()).isEqualTo(groupChannelTap.getId());
        assertThat(groupChat.getMessage()).isEqualTo(chatAddDto.getMessage());
        assertThat(groupChat.getImage()).isNull();
    }

    @Test
    void addChat_권한X() {
        //given
        User user = User.create("user", null, null, null);
        em.persist(user);

        //when
        GroupChatAddDto chatAddDto = new GroupChatAddDto("message", null, groupChannelTap.getId());
        assertThatThrownBy(() -> groupChatService.addChat(user.getId(), group.getGroupUuid(), chatAddDto))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void addChat_내용X() {
        //when
        GroupChatAddDto chatAddDto = new GroupChatAddDto(" ", null, groupChannelTap.getId());
        assertThatThrownBy(() -> groupChatService.addChat(groupUser.getId(), group.getGroupUuid(), chatAddDto))
                .isInstanceOf(GroupChatException.class);
    }

    @Test
    void addGptChat() {
        //when
        String message = "아이폰 13미니의 출시일은 언제야?";
        GroupGptChatAddDto chatAddDto = new GroupGptChatAddDto(message, groupChannelTap.getId());
        GroupGptChatAddResDto result = groupChatService.addGptChat(groupUser.getId(), group.getGroupUuid(), chatAddDto);

        //then
        GroupChat groupChat = groupChatRepository.findById(result.getUserChatId()).orElseThrow();
        assertThat(groupChat.getTapId()).isEqualTo(groupChannelTap.getId());
        assertThat(groupChat.getMessage()).isEqualTo(chatAddDto.getMessage());
        assertThat(groupChat.getImage()).isNull();

        GroupChat gptChat = groupChatRepository.findById(result.getGptChatId()).orElseThrow();
        assertThat(gptChat.getTapId()).isEqualTo(groupChannelTap.getId());
        log.info("gpt response = {}", result.getMessage());
    }
}