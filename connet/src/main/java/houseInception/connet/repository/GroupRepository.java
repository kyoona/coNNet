package houseInception.connet.repository;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.group.Group;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long>, GroupCustomRepository {

    Optional<Group> findByGroupUuidAndStatus(String groupUuid, Status status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Group g WHERE g.groupUuid = :groupUuid AND g.status = :status")
    Optional<Group> findByGroupUuidAndStatusWithLock(@Param("groupUuid") String groupUuid, @Param("status") Status status);
}
