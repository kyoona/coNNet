package houseInception.gptComm.dto;

import com.querydsl.core.annotations.QueryProjection;
import houseInception.gptComm.domain.ChatterRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

import static houseInception.gptComm.domain.ChatterRole.GPT;

@ToString
@Getter
@NoArgsConstructor
public class GptChatRoomChatResDto {

    private Long chatId;
    private String content;
    private ChatterRole writerRole;
    private UserResDto writer;
    private LocalDateTime createAt;

    @QueryProjection
    public GptChatRoomChatResDto(Long chatId, String content, ChatterRole writerRole, Long writerId, String writerName, String writerProfile, LocalDateTime createAt) {
        this.chatId = chatId;
        this.content = content;
        this.writerRole = writerRole;
        this.createAt = createAt;

        if (writerRole == GPT) {
            this.writer = null;
        }else {
            this.writer = new UserResDto(writerId, writerName, writerProfile);
        }
    }
}
