package houseInception.connet.repository;

import houseInception.connet.dto.groupChat.GroupChatResDto;

import java.util.List;
import java.util.Optional;

public interface GroupChatCustomRepository{

    Optional<Long> findGroupIdOfChat(Long chatId);

    List<GroupChatResDto> getChatList(Long tapId, int page);
}
