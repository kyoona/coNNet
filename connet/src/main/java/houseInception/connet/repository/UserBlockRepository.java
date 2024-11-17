package houseInception.connet.repository;

import houseInception.connet.domain.UserBlock;
import houseInception.connet.domain.UserBlockType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserBlockRepository extends JpaRepository<UserBlock, Long>, UserBlockCustomRepository {

    Optional<UserBlock> findByUserIdAndTargetId(Long userId, Long targetId);

    boolean existsByUserIdAndTargetId(Long userId, Long targetId);
    boolean existsByUserIdAndTargetIdAndBlockType(Long userId, Long targetId, UserBlockType blockType);
}
