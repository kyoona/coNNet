package houseInception.gptComm.externalServiceProvider.google;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import houseInception.gptComm.exception.InValidTokenException;
import houseInception.gptComm.exception.JsonParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static houseInception.gptComm.response.status.BaseErrorCode.INVALID_GOOGLE_TOKEN;

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
        String accessToken = getAccessToken(googleToken);

        String googleUserInfoEndpoint = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

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

    private String getAccessToken(String googleToken) {
        String googleAccessKeyEndpoint = "https://oauth2.googleapis.com/token";

        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("code", googleToken);
        requestBodyMap.put("client_id", clientId);
        requestBodyMap.put("client_secret", clientSecret);
        requestBodyMap.put("redirect_uri", "http://localhost:5174");
        requestBodyMap.put("grant_type", "authorization_code");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(requestBodyMap);
        } catch (JsonProcessingException e) {
            throw new JsonParseException();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        HttpEntity<String> accessTokenRequestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<GoogleAccessToken> response;
        try {
            response = restTemplate.exchange(
                    googleAccessKeyEndpoint,
                    HttpMethod.POST,
                    accessTokenRequestEntity,
                    GoogleAccessToken.class
            );
        } catch (Exception e) {
            log.error("Error while requesting access token", e);
            throw new InValidTokenException(INVALID_GOOGLE_TOKEN);
        }

        return response.getBody().getAccess_token();
    }

}
