package houseInception.gptComm.externalServiceProvider.google;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class GoogleOathProviderTest {

    @Autowired
    GoogleOathProvider oathProvider;

    @Test
    void getUserInfo() {
        String googleToken = "ya29.a0AcM612yKrF7_jlOb7k9Hg4iUa5UWP3OGhWa70RBc5aLubrw1GEdW2bJxDx5oaa3JJCLb-DfwOks1fhb7RN3V59UL-7o2cVA-qk8h1p8oYK56UuiQPD-0K9zAXeIPQGnKO1GlEAGtW1eshfrAgiMjOGRB8wAPo04CPj6shjdcaCgYKAZkSARMSFQHGX2Mio6AzJ8x8ytBlF9GK-QBnOg0175";
        GoogleUserInfo userInfo = oathProvider.getUserInfo(googleToken);
        log.info("userInfo {}", userInfo.getEmail());
    }
}