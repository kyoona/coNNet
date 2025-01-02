package houseInception.connet.service;

import houseInception.connet.domain.ChatReadLog;
import houseInception.connet.dto.ChatReadLogDto;
import houseInception.connet.repository.ChatReadLogRepository;
import houseInception.connet.service.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatReadLogService {

    private final ChatReadLogRepository chatReadLogRepository;

    @Transactional
    public Long updateReadLog(Long userId, String payload){
        ChatReadLogDto dto = ObjectMapperUtil.parseJson(payload, ChatReadLogDto.class);

        ChatReadLog chatReadLog;
        switch (dto.getType()){
            case GROUP -> chatReadLog = ChatReadLog.createGroupChatLog(userId, dto.getTapId(), dto.getChatId());
            case PRIVATE -> chatReadLog = ChatReadLog.createPrivateChatLog(userId, dto.getChatId());
            default -> throw new IllegalArgumentException("Unsupported Read Log Type: " + dto.getType());
        }

        chatReadLogRepository.save(chatReadLog);

        return chatReadLog.getChatId();
    }
}
