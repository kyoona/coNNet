package houseInception.connet.repository;

import java.util.List;
import java.util.Map;

public interface ChatReadLogCustomRepository {


    Map<Long, Long> findRecentReadLogOfTaps(List<Long> tapIdList);
}
