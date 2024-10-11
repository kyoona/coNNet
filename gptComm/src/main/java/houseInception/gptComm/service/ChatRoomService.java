package houseInception.gptComm.service;

import houseInception.gptComm.domain.User;
import houseInception.gptComm.domain.chatRoom.Chat;
import houseInception.gptComm.domain.chatRoom.ChatRoom;
import houseInception.gptComm.domain.chatRoom.ChatRoomType;
import houseInception.gptComm.domain.chatRoom.ChatRoomUser;
import houseInception.gptComm.dto.ChatAddDto;
import houseInception.gptComm.dto.GptChatResDto;
import houseInception.gptComm.exception.ChatRoomException;
import houseInception.gptComm.externalServiceProvider.GptApiProvider;
import houseInception.gptComm.repository.ChatRoomRepository;
import houseInception.gptComm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.gptComm.domain.Status.ALIVE;
import static houseInception.gptComm.domain.chatRoom.ChatRoomType.GPT;
import static houseInception.gptComm.response.status.BaseErrorCode.NO_SUCH_CHATROOM;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final GptApiProvider gptApiProvider;

    @Transactional
    public GptChatResDto addGptChat(Long userId, ChatAddDto chatAddDto) {
        User user = userRepository.findById(userId).orElseThrow();

        String chatRoomUuid = chatAddDto.getChatRoomUuid();
        checkExistChatRoom(chatRoomUuid, GPT);

        ChatRoom chatRoom;
        if (chatRoomUuid == null) {
            chatRoom = ChatRoom.createGptRoom(user);
        } else {
            chatRoom = findChatRoomByUuid(chatRoomUuid);
        }

        String gptResponse = gptApiProvider.getChatCompletionWithTitle(chatAddDto.getMessage());
        String title = extractTitle(gptResponse);
        String responseContent = extractResponseContent(gptResponse);

        chatRoom.setTitle(title);

        ChatRoomUser writer = chatRoom.getChatRoomUsers().get(0);
        Chat userChat = chatRoom.addUserChatToGpt(writer, chatAddDto.getMessage());
        Chat gptChat = chatRoom.addGptChat(responseContent);

        chatRoomRepository.save(chatRoom);

        return new GptChatResDto(chatRoom.getChatRoomUuid(), title, userChat.getId(), gptChat.getId(), responseContent);
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

    private void checkExistChatRoom(String chatRoomUuid, ChatRoomType chatRoomType) {
        if(chatRoomUuid != null && !chatRoomRepository.existsByChatRoomUuidAndChatRoomTypeAndStatus(chatRoomUuid, chatRoomType, ALIVE)){
            throw new ChatRoomException(NO_SUCH_CHATROOM);
        }
    }

    private ChatRoom findChatRoomByUuid(String chatRoomUuid){
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomUuidAndStatus(chatRoomUuid, ALIVE).orElse(null);
        if (chatRoom == null) {
            throw new ChatRoomException(NO_SUCH_CHATROOM);
        }

        return chatRoom;
    }
}
