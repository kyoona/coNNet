package houseInception.connet.repository;

import houseInception.connet.domain.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserBlockRepository extends JpaRepository<UserBlock, Long>, UserBlockCustomRepository {

    boolean existsByUserIdAndTargetId(Long userId, Long targetId);
}
