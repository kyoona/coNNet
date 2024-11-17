package houseInception.socket;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.User;
import houseInception.connet.jwt.JwtTokenProvider;
import houseInception.connet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
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
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);
        if (isInvalidToken(token) || isInValidUser(token)) {
            return false;
        }

        saveUserIdInThreadLocal(token);
        return true;
    }

    private void saveUserIdInThreadLocal(String token){
        String userEmail = tokenProvider.getUserPk(token);
        User user = userRepository.findByEmailAndStatus(userEmail, Status.ALIVE).orElseThrow();

        MDC.put("socketUserId", String.valueOf(user.getId()));
    }

    private boolean isInvalidToken(String token){
        return !tokenProvider.validateToken(token);
    }

    private boolean isInValidUser(String token){
        String userEmail = tokenProvider.getUserPk(token);
        return !userRepository.existsByEmailAndStatus(userEmail, Status.ALIVE);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
