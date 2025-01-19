package houseInception.connet.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultIdDto {

    private Long id;

    public static DefaultIdDto get(Long id){
        return new DefaultIdDto(id);
    }
}
