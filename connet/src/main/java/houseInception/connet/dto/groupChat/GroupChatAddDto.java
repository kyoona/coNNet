package houseInception.connet.dto.groupChat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatAddDto {

    private String message;
    private MultipartFile image;

    @NotNull
    private Long tapId;
}
