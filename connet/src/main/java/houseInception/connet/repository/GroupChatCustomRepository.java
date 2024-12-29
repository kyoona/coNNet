package houseInception.connet.repository;

import houseInception.connet.dto.groupChat.GroupChatResDto;

import java.util.List;

public interface GroupChatCustomRepository{

    List<GroupChatResDto> getChatList(Long tapId, int page);
}
