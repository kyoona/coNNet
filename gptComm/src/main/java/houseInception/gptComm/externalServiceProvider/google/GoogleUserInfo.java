package houseInception.gptComm.externalServiceProvider.google;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserInfo {
    private String iss;
    private String azp;
    private String aud;
    private String sub;
    private String email;
    private boolean emailVerified;
    private long nbf;
    private String name;
    private String picture;
    private String givenName;
    private String familyName;
    private long iat;
    private long exp;
    private String jti;
    private String alg;
    private String kid;
    private String typ;
}
