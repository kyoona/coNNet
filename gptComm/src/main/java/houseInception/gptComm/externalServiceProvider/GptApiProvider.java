package houseInception.gptComm.externalServiceProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class GptApiProvider {

    private final RestTemplate restTemplate;

    @Value("${gpt.key}")
    private String GPT_API_KEY = "your_openai_api_key";
    private final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String getChatCompletion(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(GPT_API_KEY);  // Authorization 헤더에 Bearer 토큰 추가

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", content);
        requestBody.put("messages", new Map[] {userMessage});

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                OPENAI_API_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getBody();
    }

    public String getChatCompletionWithTitle(String content){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(GPT_API_KEY);  // Authorization 헤더에 Bearer 토큰 추가

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "<" + content +">이 메세지에 대해 응답과 제목을 생성해줘. 제목은 20자 이내, 명사형으로 끝나게 메세지 내용을 요약해서 응답의 가장 처음 <<>>안에 넣어줘.그리고 제목과 본문사이에 엔터는 없어.");
        requestBody.put("messages", new Map[] {userMessage});

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                OPENAI_API_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getBody();
    }
}
