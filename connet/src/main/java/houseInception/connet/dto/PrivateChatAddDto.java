package houseInception.connet.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivateChatAddDto {

    private String chatRoomUuid;
    private String message;
    private MultipartFile image;
}
