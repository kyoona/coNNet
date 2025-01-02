package houseInception.connet.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class GroupUpdateDto {

    @Size(max = 30)
    @NotBlank
    private String groupName;
    private MultipartFile groupProfile;

    @Size(max = 30)
    private String groupDescription;

    @Size(max = 10)
    private List<String> addedTags;

    @Size(max = 10)
    private List<String> deletedTags;

    @NotNull
    private Boolean isOpen;
}
