package houseInception.gptComm.service;

import houseInception.gptComm.domain.User;
import houseInception.gptComm.domain.chatRoom.Chat;
import houseInception.gptComm.domain.chatRoom.ChatRoom;
import houseInception.gptComm.domain.chatRoom.ChatRoomType;
import houseInception.gptComm.domain.chatRoom.ChatRoomUser;
import houseInception.gptComm.dto.ChatAddDto;
import houseInception.gptComm.dto.GptChatResDto;
import houseInception.gptComm.exception.ChatRoomException;
import houseInception.gptComm.externalServiceProvider.gpt.GptApiProvider;
import houseInception.gptComm.externalServiceProvider.gpt.GptResDto;
import houseInception.gptComm.repository.ChatRoomRepository;
import houseInception.gptComm.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.gptComm.domain.Status.ALIVE;
import static houseInception.gptComm.domain.chatRoom.ChatRoomType.GPT;
import static houseInception.gptComm.response.status.BaseErrorCode.NOT_CHATROOM_USER;
import static houseInception.gptComm.response.status.BaseErrorCode.NO_SUCH_CHATROOM;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final GptApiProvider gptApiProvider;
    private final EntityManager em;

    @Transactional
    public GptChatResDto addGptChat(Long userId, ChatAddDto chatAddDto) {
        User user = userRepository.findById(userId).orElseThrow();

        String chatRoomUuid = chatAddDto.getChatRoomUuid();
        checkExistChatRoom(chatRoomUuid, GPT);

        ChatRoom chatRoom;
        String content;
        if (chatRoomUuid == null) {
            chatRoom = ChatRoom.createGptRoom(user);

            GptResDto gptResDto = gptApiProvider.getChatCompletionWithTitle(chatAddDto.getMessage());

            chatRoom.setTitle(gptResDto.getTitle());
            chatRoomRepository.save(chatRoom);

            content = gptResDto.getContent();
        } else {
            chatRoom = findChatRoomByUuid(chatRoomUuid);
            checkChatRoomUser(chatRoom.getId(), userId);
            content = gptApiProvider.getChatCompletion(chatAddDto.getMessage());
        }

        ChatRoomUser writer = chatRoom.getChatRoomUsers().get(0);
        Chat userChat = chatRoom.addUserChatToGpt(writer, chatAddDto.getMessage());
        Chat gptChat = chatRoom.addGptChat(content);
        em.flush();

        return new GptChatResDto(chatRoom.getChatRoomUuid(), chatRoom.getTitle(), userChat.getId(), gptChat.getId(), content);
    }

    private void checkExistChatRoom(String chatRoomUuid, ChatRoomType chatRoomType) {
        if(chatRoomUuid != null && !chatRoomRepository.existsByChatRoomUuidAndChatRoomTypeAndStatus(chatRoomUuid, chatRoomType, ALIVE)){
            throw new ChatRoomException(NO_SUCH_CHATROOM);
        }
    }

    private void checkChatRoomUser(Long chatRoomId, Long userId){
        if(!chatRoomRepository.existsChatRoomUser(chatRoomId, userId, ALIVE)) {
            throw new ChatRoomException(NOT_CHATROOM_USER);
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
