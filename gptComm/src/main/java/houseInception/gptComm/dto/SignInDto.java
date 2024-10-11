package houseInception.gptComm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInDto {

    @NotBlank
    private String googleToken;
}
