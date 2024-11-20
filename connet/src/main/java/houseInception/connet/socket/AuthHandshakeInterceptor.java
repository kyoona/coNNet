package houseInception.connet.socket;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.User;
import houseInception.connet.jwt.JwtTokenProvider;
import houseInception.connet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = getToken(request.getHeaders());

        String userEmail = tokenProvider.getUserPk(token);
        User user = userRepository.findByEmailAndStatus(userEmail, Status.ALIVE).orElseThrow();

        attributes.put("Socket-User-Id", user.getId());
        return true;
    }

    private String getToken(HttpHeaders headers){
        return headers.getFirst("Authorization").substring(7);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
