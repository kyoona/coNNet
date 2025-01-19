package houseInception.connet.repository;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository{

    Optional<User> findByIdAndStatus(Long userId, Status status);
    Optional<User> findByEmailAndStatus(String email, Status status);

    boolean existsByEmailAndStatus(String email, Status status);
    boolean existsByIdAndStatus(Long userId, Status status);
    boolean existsByRefreshTokenAndStatus(String refreshToken, Status status);
}
