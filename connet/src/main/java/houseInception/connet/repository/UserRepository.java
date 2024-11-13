package houseInception.connet.repository;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository{

    Optional<User> findByEmailAndStatus(String email, Status status);
    boolean existsByEmailAndStatus(String email, Status status);
    boolean existsByRefreshTokenAndStatus(String refreshToken, Status status);
}
