package houseInception.connet.dto.login;

import lombok.Getter;

@Getter
public class TokenResDto {

    private String grantType = "Bearer";
    private String accessToken;
    private String refreshToken;

    public TokenResDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
