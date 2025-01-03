package houseInception.connet.service;

import houseInception.connet.domain.alarm.FriendAlarm;
import houseInception.connet.domain.alarm.GroupAlarm;
import houseInception.connet.domain.group.Group;
import houseInception.connet.domain.user.User;
import houseInception.connet.dto.alarm.AlarmCountResDto;
import houseInception.connet.dto.alarm.AlarmResDto;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class AlarmServiceTest {

    @Autowired
    AlarmService alarmService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    User user1;
    User user2;

    @BeforeEach
    void beforeEach(){
        user1 = User.create("user1", null, null, null);
        user2 = User.create("user2", null, null, null);
        em.persist(user1);
        em.persist(user2);
    }

    @AfterEach
    void afterEach(){
        userRepository.deleteAll();
    }

    @Test
    void getAlarmCount() {
        //given
        Group group = Group.create(user2, "groupName", null, null, 3, false);
        em.persist(group);

        FriendAlarm friendAlarm1 = FriendAlarm.createRequestAlarm(user1, user2);
        FriendAlarm friendAlarm2 = FriendAlarm.createRequestAlarm(user1, user2);
        friendAlarm2.check();

        GroupAlarm groupAlarm = GroupAlarm.createInviteAlarm(user1, group);

        em.persist(friendAlarm1);
        em.persist(friendAlarm2);
        em.persist(groupAlarm);

        //when
        AlarmCountResDto result = alarmService.getUncheckedAlarmCount(user1.getId());

        //then
        assertThat(result.getCount()).isEqualTo(2);
    }

    @Test
    void getAlarmList() {
        //given
        Group group = Group.create(user2, "groupName", null, null, 3, false);
        em.persist(group);

        FriendAlarm friendAlarm1 = FriendAlarm.createRequestAlarm(user1, user2);
        FriendAlarm friendAlarm2 = FriendAlarm.createRequestAlarm(user1, user2);
        friendAlarm2.check();

        GroupAlarm groupAlarm = GroupAlarm.createInviteAlarm(user1, group);

        em.persist(friendAlarm1);
        em.persist(friendAlarm2);
        em.persist(groupAlarm);

        //when
        List<AlarmResDto> result = alarmService.getAlarmList(user1.getId());

        //then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(AlarmResDto::isChecked)
                .containsExactly(false, true, false);
        assertThat(friendAlarm1.isChecked()).isTrue();
        assertThat(friendAlarm2.isChecked()).isTrue();
        assertThat(groupAlarm.isChecked()).isTrue();
    }
}