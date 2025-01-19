package houseInception.connet.dto.privateRoom;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivateChatAddDto {

    private String message;
    private MultipartFile image;
}
