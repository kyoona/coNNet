package houseInception.connet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrivateChatAddDto {

    private String chatRoomUuid;
    private String message;
    List<MultipartFile> images;
}
