package houseInception.connet.repository;

import houseInception.connet.domain.alarm.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, AlarmCustomRepository {
    List<Alarm> findByUserId(Long userId);
}
