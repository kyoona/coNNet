package houseInception.connet.repository;

import houseInception.connet.domain.GroupInvite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long>, GroupInviteCustomRepository {

    boolean existsByGroupUuidAndInvitee(String groupUuid, Long inviteeId);
}
