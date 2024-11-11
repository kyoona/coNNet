package houseInception.gptComm.repository;

import houseInception.gptComm.domain.Status;
import houseInception.gptComm.domain.gptRoom.GptRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GptRoomRepository extends JpaRepository<GptRoom, Long>, GptRoomCustomRepository {

    Optional<GptRoom> findByGptRoomUuidAndStatus(String gptRoomUuid, Status status);

    boolean existsByGptRoomUuidAndStatus(String gptRoomUuid, Status status);
}
