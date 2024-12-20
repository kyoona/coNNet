package houseInception.connet.repository;

import java.util.List;

public interface GroupCustomRepository {

    boolean existUserInGroup(Long userId, String groupUuid);
}
