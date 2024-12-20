package houseInception.connet.repository;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long>, GroupCustomRepository {

    Optional<Group> findByGroupUuid(String groupUuid);

    boolean existsByGroupUuidAndStatus(String groupUuid, Status status);
}
