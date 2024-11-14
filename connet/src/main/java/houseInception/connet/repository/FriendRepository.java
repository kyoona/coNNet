package houseInception.connet.repository;

import houseInception.connet.domain.Friend;
import houseInception.connet.domain.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendCustomRepository {

    Optional<Friend> findBySenderIdAndReceiverIdAndAcceptStatus(Long senderId, Long recipientId, FriendStatus acceptStatus);

    boolean existsBySenderIdAndReceiverIdAndAcceptStatus(Long senderId, Long recipientId, FriendStatus acceptStatus);
}
