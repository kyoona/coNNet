package houseInception.connet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GptRoomChatAddDto {

    private String chatRoomUuid;

    @NotBlank
    private String message;
}
