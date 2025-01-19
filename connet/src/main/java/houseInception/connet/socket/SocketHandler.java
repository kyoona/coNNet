package houseInception.connet.socket;

import houseInception.connet.service.ChatReadLogService;
import houseInception.connet.service.UserService;
import houseInception.connet.socketManager.SocketManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
@Component
public class SocketHandler extends TextWebSocketHandler {

    private final SocketManager socketManager;
    private final UserService userService;
    private final ChatReadLogService chatReadLogService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);

        Long userId = (Long) session.getAttributes().get("Socket-User-Id");
        chatReadLogService.updateReadLog(userId, message.getPayload());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        Long userId = (Long) session.getAttributes().get("Socket-User-Id");
        socketManager.addSocket(userId, session);
        userService.setUserActive(userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = socketManager.deleteSocket(session);
        if(userId != null){
            userService.setUserInActive(userId);
        }
        
        super.afterConnectionClosed(session, status);
    }
}
