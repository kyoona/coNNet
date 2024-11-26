package houseInception.connet.socket;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.User;
import houseInception.connet.jwt.JwtTokenProvider;
import houseInception.connet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = getToken(request.getURI());
        String userEmail = checkValidToken(token);

        if (userEmail == null) {
            return false;
        }

        User user = userRepository.findByEmailAndStatus(userEmail, Status.ALIVE).orElseThrow();
        attributes.put("Socket-User-Id", user.getId());

        return true;
    }

    private String  checkValidToken(String token){
        if(token != null && tokenProvider.validateToken(token)){
            return tokenProvider.getUserPk(token);
        }

        return null;
    }

    private String getToken(URI uri){
        return UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("token");
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
