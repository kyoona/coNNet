package houseInception.connet.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateDto {

    @NotNull
    private String userName;
    @Pattern(regexp = "\\S*", message = "userDescription must not be blank if provided")
    private String userDescription;
    private MultipartFile userProfile;
}
