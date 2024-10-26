package houseInception.gptComm.externalServiceProvider.google;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAccessToken {

    private String access_token;
    private int expires_in;
    private String token_type;
    private String scope;
    private String refresh_token;
}
