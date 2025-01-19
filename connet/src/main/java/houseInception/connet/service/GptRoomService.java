package houseInception.connet.service;

import houseInception.connet.domain.user.User;
import houseInception.connet.domain.gptRoom.GptRoom;
import houseInception.connet.domain.gptRoom.GptRoomChat;
import houseInception.connet.domain.gptRoom.GptRoomUser;
import houseInception.connet.dto.*;
import houseInception.connet.dto.GptRoom.*;
import houseInception.connet.exception.GptRoomException;
import houseInception.connet.externalServiceProvider.gpt.GptApiProvider;
import houseInception.connet.externalServiceProvider.gpt.GptResDto;
import houseInception.connet.repository.GptRoomRepository;
import houseInception.connet.service.util.CommonDomainService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.response.status.BaseErrorCode.NOT_CHATROOM_USER;
import static houseInception.connet.response.status.BaseErrorCode.NO_SUCH_CHATROOM;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GptRoomService {

    private final GptRoomRepository gptRoomRepository;
    private final EntityManager em;
    private final CommonDomainService domainService;
    private final GptApiProvider gptApiProvider;

    @Transactional
    public GptChatResDto addGptChat(Long userId, GptRoomChatAddDto gptRoomChatAddDto) {
        User user = domainService.findUser(userId);

        String chatRoomUuid = gptRoomChatAddDto.getChatRoomUuid();

        GptRoom gptRoom;
        String content;
        if (chatRoomUuid == null) {
            gptRoom = GptRoom.createGptRoom(user);

            GptResDto gptResDto = gptApiProvider.getChatCompletionWithTitle(gptRoomChatAddDto.getMessage());

            gptRoom.setTitle(gptResDto.getTitle());
            gptRoomRepository.save(gptRoom);

            content = gptResDto.getContent();
        } else {
            gptRoom = findGptRoomByUuid(chatRoomUuid);
            checkGptRoomUser(gptRoom.getId(), userId);
            Optional<String> lastGptChat = gptRoomRepository.getLastGptChat(gptRoom.getId());
            content =  lastGptChat.isEmpty()
                    ? gptApiProvider.getChatCompletion(gptRoomChatAddDto.getMessage())
                    : gptApiProvider.getChatCompletionWithPrevContent(gptRoomChatAddDto.getMessage(), lastGptChat.get());
        }

        GptRoomUser writer = gptRoom.getGptRoomUsers().get(0);
        GptRoomChat userChat = gptRoom.addUserChat(writer, gptRoomChatAddDto.getMessage());
        GptRoomChat gptChat = gptRoom.addGptChat(content);
        em.flush();

        return new GptChatResDto(
                gptRoom.getGptRoomUuid(),
                gptRoom.getTitle(),
                userChat.getId(),
                userChat.getCreatedAt(),
                gptChat.getId(),
                gptChat.getCreatedAt(),
                content
        );
    }

    @Transactional
    public Long updateGptRoom(Long userId, String gptRoomUuid, String title) {
        GptRoom gptRoom = findGptRoomByUuid(gptRoomUuid);
        checkGptRoomUser(gptRoom.getId(), userId);

        gptRoom.updateGptRoom(title);

        return gptRoom.getId();
    }

    @Transactional
    public Long deleteGptRoom(Long userId, String gptRoomUuid) {
        GptRoom gptRoom = findGptRoomByUuid(gptRoomUuid);
        checkGptRoomUser(gptRoom.getId(), userId);

        gptRoom.delete();

        return gptRoom.getId();
    }

    public DataListResDto<GptRoomListResDto> getGptChatRoomList(Long userId, int page) {
        List<GptRoomListResDto> gptRoomList = gptRoomRepository.getGptRoomListByUserId(userId, page);

        return new DataListResDto<>(page, gptRoomList);
    }

    public GptChatRoomChatListResDto getGptChatRoomChatList(Long userId, String gptRoomUuid, int page) {
        GptRoom gptRoom = findGptRoomByUuid(gptRoomUuid);
        checkGptRoomUser(gptRoom.getId(), userId);

        List<GptRoomChatResDto> chatList = gptRoomRepository.getGptChatRoomChatList(gptRoom.getId(), page);

        return new GptChatRoomChatListResDto(gptRoomUuid, gptRoom.getTitle(), page, chatList);
    }

    private void checkGptRoomUser(Long gptRoomId, Long userId){
        if(!gptRoomRepository.existsGptRoomUser(gptRoomId, userId, ALIVE)) {
            throw new GptRoomException(NOT_CHATROOM_USER);
        }
    }

    private GptRoom findGptRoomByUuid(String gptRoomUuid){
        return gptRoomRepository.findByGptRoomUuidAndStatus(gptRoomUuid, ALIVE)
                .orElseThrow(() -> new GptRoomException(NO_SUCH_CHATROOM));
    }
}
