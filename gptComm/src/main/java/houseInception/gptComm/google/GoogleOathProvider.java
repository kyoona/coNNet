package houseInception.gptComm.google;

import houseInception.gptComm.exception.InValidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static houseInception.gptComm.response.status.BaseErrorCode.INVALID_GOOGLE_TOKEN;

@RequiredArgsConstructor
@Service
public class GoogleOathProvider {

    private final RestTemplate restTemplate;

    public GoogleUserInfo getUserInfo(String googleToken){

        String googleUserInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                    googleUserInfoEndpoint,
                    HttpMethod.GET,
                    entity,
                    GoogleUserInfo.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new InValidTokenException(INVALID_GOOGLE_TOKEN);
        }
    }
}
