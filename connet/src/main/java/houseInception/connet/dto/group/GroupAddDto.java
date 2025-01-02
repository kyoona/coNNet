package houseInception.connet.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupAddDto {

    @NotBlank
    private String groupName;
    private MultipartFile groupProfile;
    private String groupDescription;

    @Size(max = 5)
    private List<String> tags;

    @Positive
    private int userLimit;
    private Boolean isOpen;
}
