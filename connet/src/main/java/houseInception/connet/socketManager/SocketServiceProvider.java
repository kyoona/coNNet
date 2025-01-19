package houseInception.connet.socketManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import houseInception.connet.exception.SocketException;
import houseInception.connet.socketManager.dto.PrivateChatSocketDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static houseInception.connet.response.status.BaseErrorCode.CANT_NOT_PARSE_SOCKET_MESSAGE;

@RequiredArgsConstructor
@Component
public class SocketServiceProvider {

    private final SocketManager socketManager;
    private final ObjectMapper objectMapper;

    public boolean sendMessage(Long userId, Object message){
        WebSocketSession socketSession = socketManager.getSocketSession(userId);
        if (socketSession == null) {
            return false;
        }

        try {
            TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(message));
            socketSession.sendMessage(textMessage);

            return true;
        } catch (IOException e) {
            throw new SocketException(CANT_NOT_PARSE_SOCKET_MESSAGE);
        }
    }

    public boolean isUserConnectedSocket(Long userId){
        if (socketManager.getSocketSession(userId) == null) {
            return false;
        } else {
            return true;
        }
    }
}
