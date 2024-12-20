package houseInception.connet.repository;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long>, GroupCustomRepository {

    boolean existsByGroupUuidAndStatus(String groupUuid, Status status);
}
