package houseInception.connet.repository;

import houseInception.connet.domain.group.GroupTag;

import java.util.List;

public interface GroupCustomRepository {

    List<GroupTag> findGroupTagsInGroup(Long groupId);
}
