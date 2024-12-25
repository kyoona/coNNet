package houseInception.connet.dto.groupChat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupGptChatAddDto {

    @NotBlank
    private String message;

    @NotNull
    private Long tapId;
}
