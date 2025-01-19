package houseInception.connet.externalServiceProvider.gpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import houseInception.connet.exception.JsonParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RequiredArgsConstructor
@Service
public class GptApiProvider {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gpt.key}")
    private String GPT_API_KEY;

    private final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String getChatCompletion(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(GPT_API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("max_completion_tokens", 16384);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "[" + content + "]이 메세지에 응답을 생성해줘.");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(userMessage);
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // API 요청
        ResponseEntity<String> response = restTemplate.exchange(
                OPENAI_API_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        try {
            return objectMapper.readValue(response.getBody(), ChatCompletionResponse.class).getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            throw new JsonParseException();
        }
    }

    public String getChatCompletionWithPrevContent(String content, String prevContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(GPT_API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("max_completion_tokens", 16384);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "이전에 저가 생성한 응답과 새로운 질문이 연관이 있다면 이전 응답을 기반으로 응답을 생성해줘. 만약 관련된 주제나 질의가 아니라면 연관짓지 말고 새로운 응답을 생성해줘." +
                "이전 응답 : [" + prevContent +"]" +
                "질문 : [" + content + "]");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(userMessage);
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // API 요청
        ResponseEntity<String> response = restTemplate.exchange(
                OPENAI_API_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        try {
            return objectMapper.readValue(response.getBody(), ChatCompletionResponse.class).getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            throw new JsonParseException();
        }
    }

    public GptResDto getChatCompletionWithTitle(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(GPT_API_KEY);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "[" + content + "] 이 메세지에 대한 제목과 응답을 생성해줘. 제목은 메세지 내용을 요약해서 20자 이내로 명사형으로 끝나. 제목은 응답의 가장 처음 <<>>안에 넣어줘. 제목과 응답 사이에 문자,엔터는 없어.");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("max_completion_tokens", 16384);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(userMessage);
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // API 요청
        ResponseEntity<String> response = restTemplate.exchange(
                OPENAI_API_URL,
                HttpMethod.POST,
                entity,
                String.class
        );
        
        try {

            String resMessage = objectMapper.readValue(response.getBody(), ChatCompletionResponse.class).getChoices().get(0).getMessage().getContent();
            String title = extractTitle(resMessage);
            String responseContent = extractResponseContent(resMessage);

            return new GptResDto(title, responseContent);
        } catch (Exception e) {
            throw new JsonParseException();
        }
    }

    private String extractTitle(String response) {
        int start = response.indexOf("<<") + 2;
        int end = response.indexOf(">>");
        return response.substring(start, end).trim();
    }

    private String extractResponseContent(String response) {
        int end = response.indexOf(">>") + 2;
        return response.substring(end).trim();
    }
}
