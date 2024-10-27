package houseInception.gptComm.repository;

public interface FriendCustomRepository {

    boolean existsFriend(Long userId, Long targetId);
}
