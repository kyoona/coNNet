package houseInception.connet.socketManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatterResDto {

    private Long userId;
    private String userName;
    private String userProfile;
}
