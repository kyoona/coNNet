package houseInception.gptComm.externalServiceProvider.google;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserInfo {
    private String family_name;
    private String name;
    private String picture;
    private String email;
    private String given_name;
    private String id;
    private boolean verified_email;
}
