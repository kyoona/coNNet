package houseInception.gptComm.filter.security;

import houseInception.gptComm.filter.MdcFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.POST;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SpringConfig {

    private final MdcFilter mdcFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserValidationFilter userValidationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(POST, "/login/sign-in").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(mdcFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(userValidationFilter, JwtAuthenticationFilter.class)
                .exceptionHandling(exceptionConfig -> exceptionConfig
                        .authenticationEntryPoint(authenticationEntryPoint))
                .build();
    }
}
