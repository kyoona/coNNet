package houseInception.connet.externalServiceProvider.google;

import houseInception.connet.exception.InValidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static houseInception.connet.response.status.BaseErrorCode.INVALID_GOOGLE_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleOathProvider {

    private final RestTemplate restTemplate;

    @Value("${google.id}")
    private String clientId;

    @Value("${google.password}")
    private String clientSecret;

    public GoogleUserInfo getUserInfo(String googleToken){
        String googleUserInfoEndpoint = "https://oauth2.googleapis.com/tokeninfo?id_token=" + googleToken;

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                    googleUserInfoEndpoint,
                    HttpMethod.POST,
                    entity,
                    GoogleUserInfo.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new InValidTokenException(INVALID_GOOGLE_TOKEN);
        }
    }
}
