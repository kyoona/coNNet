package houseInception.connet.dto.GptRoom;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GptRoomUpdateDto {

    @NotBlank
    private String title;
}
