package houseInception.connet.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultUuidDto {

    private String uuid;

    public static DefaultUuidDto get(String uuid){
        return new DefaultUuidDto(uuid);
    }
}
