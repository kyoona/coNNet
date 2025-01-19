package houseInception.connet.repository;

import houseInception.connet.dto.groupChat.GroupChatResDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GroupChatCustomRepository{

    Optional<Long> findGroupIdOfChat(Long chatId);
    Map<Long, Long> findRecentGroupChatOfTaps(List<Long> channelTapList);
    Optional<String> findLastGptChatOfTap(Long groupId);

    List<GroupChatResDto> getChatList(Long tapId, int page);

}
