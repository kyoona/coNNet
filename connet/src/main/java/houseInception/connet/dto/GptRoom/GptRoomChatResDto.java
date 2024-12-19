package houseInception.connet.dto.GptRoom;

import com.querydsl.core.annotations.QueryProjection;
import houseInception.connet.domain.ChatterRole;
import houseInception.connet.dto.DefaultUserResDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

import static houseInception.connet.domain.ChatterRole.GPT;

@ToString
@Getter
@NoArgsConstructor
public class GptRoomChatResDto {

    private Long chatId;
    private String content;
    private ChatterRole writerRole;
    private DefaultUserResDto writer;
    private LocalDateTime createAt;

    @QueryProjection
    public GptRoomChatResDto(Long chatId, String content, ChatterRole writerRole, Long writerId, String writerName, String writerProfile, LocalDateTime createAt) {
        this.chatId = chatId;
        this.content = content;
        this.writerRole = writerRole;
        this.createAt = createAt;

        if (writerRole == GPT) {
            this.writer = null;
        }else {
            this.writer = new DefaultUserResDto(writerId, writerName, writerProfile);
        }
    }
}
