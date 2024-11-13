package houseInception.connet.externalServiceProvider.google;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class GoogleOathProviderTest {

    @Autowired
    GoogleOathProvider oathProvider;

/*    @Test
    void getUserInfo() {
        String googleToken = "4/0AVG7fiRbtKc1FDeJcI1DqIyL1QZnNOriMUewNK3cPzIOFj6nhnRyOEx3KX17KfhfZGUg9A";
        GoogleUserInfo userInfo = oathProvider.getUserInfo(googleToken);
        log.info("userInfo {}", userInfo.getEmail());
    }*/
}