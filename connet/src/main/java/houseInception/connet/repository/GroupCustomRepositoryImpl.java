package houseInception.connet.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.group.GroupTag;
import houseInception.connet.domain.group.QGroupTag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static houseInception.connet.domain.group.QGroupTag.groupTag;

@RequiredArgsConstructor
@Repository
public class GroupCustomRepositoryImpl implements GroupCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public List<GroupTag> findGroupTagsInGroup(Long groupId) {
        return query
                .selectFrom(groupTag)
                .where(groupTag.group.id.eq(groupId))
                .fetch();
    }
}
