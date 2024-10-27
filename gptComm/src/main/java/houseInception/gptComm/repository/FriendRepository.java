package houseInception.gptComm.repository;

import houseInception.gptComm.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendCustomRepository {

}
