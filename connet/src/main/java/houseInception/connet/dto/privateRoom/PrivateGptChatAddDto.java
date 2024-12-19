package houseInception.connet.dto.privateRoom;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrivateGptChatAddDto {

    @NotBlank
    private String message;
}
