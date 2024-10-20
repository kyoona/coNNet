package houseInception.gptComm;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final String CORS_URL_PATTERN = "/**";
    private static final String CORS_URL = "*";
    private static final String CORS_METHOD = "*";

    @Override
    public void addCorsMappings(CorsRegistry registry) { //cors 허용
        registry.addMapping(CORS_URL_PATTERN)
                .allowedOrigins(CORS_URL)
                .allowedMethods(CORS_METHOD);
    }
}
