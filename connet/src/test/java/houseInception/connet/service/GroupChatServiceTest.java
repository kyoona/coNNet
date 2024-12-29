package houseInception.connet.service;

import houseInception.connet.domain.ChatEmoji;
import houseInception.connet.domain.GroupChat;
import houseInception.connet.domain.User;
import houseInception.connet.domain.channel.Channel;
import houseInception.connet.domain.channel.ChannelTap;
import houseInception.connet.domain.group.Group;
import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.dto.groupChat.*;
import houseInception.connet.exception.ChannelException;
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

import java.util.List;

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

    @Test
    void getChatList() {
        //given
        GroupUser groupUser1 = groupRepository.findGroupUser(group.getId(), groupUser.getId()).orElseThrow();
        GroupChat chat1 = GroupChat.createUserToUser(groupUser1, groupChannelTap.getId(), "message", null);
        GroupChat chat2 = GroupChat.createUserToUser(groupUser1, groupChannelTap.getId(), "message", null);
        GroupChat chat3 = GroupChat.createGptToUser(groupChannelTap.getId(), "message");
        em.persist(chat1);
        em.persist(chat2);
        em.persist(chat3);

        //when
        List<GroupChatResDto> result = groupChatService.getChatList(groupUser.getId(), group.getGroupUuid(), groupChannelTap.getId(), 1).getData();

        //then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(GroupChatResDto::getChatId)
                .containsExactly(chat1.getId(), chat2.getId(), chat3.getId());
    }

    @Test
    void getChatList_권한X() {
        //given
        User user = User.create("user", null, null, null);
        em.persist(user);

        //when
        assertThatThrownBy(() -> groupChatService.getChatList(user.getId(), group.getGroupUuid(), groupChannelTap.getId(), 1))
                .isInstanceOf(GroupException.class);
    }

    @Test
    void getChatList_그룹멤버이지만_다른그룹_탭조회() {
        //given
        Group group2 = Group.create(groupOwner, "group2", null, null, 10, true);
        em.persist(group2);

        Channel channel2 = Channel.create(group2.getId(), "channel2");
        ChannelTap tap2 = channel2.addTap("tap2");
        em.persist(channel2);

        //when
        assertThatThrownBy(() -> groupChatService.getChatList(groupUser.getId(), group.getGroupUuid(), tap2.getId(), 1))
                .isInstanceOf(ChannelException.class);
    }
}