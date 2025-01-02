package houseInception.connet.repository;

import houseInception.connet.domain.ChatReadLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatReadLogRepository extends JpaRepository<ChatReadLog, Long> {
}
