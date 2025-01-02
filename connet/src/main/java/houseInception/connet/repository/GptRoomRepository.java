package houseInception.connet.repository;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.gptRoom.GptRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GptRoomRepository extends JpaRepository<GptRoom, Long>, GptRoomCustomRepository {

    Optional<GptRoom> findByGptRoomUuidAndStatus(String gptRoomUuid, Status status);
}
