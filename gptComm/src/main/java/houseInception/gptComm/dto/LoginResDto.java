package houseInception.gptComm.dto;

import lombok.Getter;

@Getter
public class LoginResDto {

    private String grantType = "Bearer";
    private String accessToken;
    private String refreshToken;

    public LoginResDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
