package houseInception.connet.service;

import houseInception.connet.domain.ChatReadLog;
import houseInception.connet.domain.ChatRoomType;
import houseInception.connet.domain.user.User;
import houseInception.connet.dto.ChatReadLogDto;
import houseInception.connet.repository.ChatReadLogRepository;
import houseInception.connet.service.util.ObjectMapperUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class ChatReadLogServiceTest {

    @Autowired
    ChatReadLogService chatReadLogService;
    @Autowired
    ChatReadLogRepository chatReadLogRepository;
    @Autowired
    EntityManager em;

    @Test
    void updateReadLog() {
        //given
        User user = User.create("user", null, null, null);
        em.persist(user);

        //when
        ChatReadLogDto logDto = new ChatReadLogDto(ChatRoomType.GROUP, null, null, null);
        Long resultId = chatReadLogService.updateReadLog(user.getId(), ObjectMapperUtil.toJson(logDto));

        //then
        ChatReadLog chatReadLog = chatReadLogRepository.findById(resultId).get();
        assertThat(chatReadLog.getUserId()).isEqualTo(user.getId());
    }
}