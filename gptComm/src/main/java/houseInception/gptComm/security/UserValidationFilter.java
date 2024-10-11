package houseInception.gptComm.security;

import houseInception.gptComm.security.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserValidationFilter extends OncePerRequestFilter {

    private final CustomUserDetailService userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() != null){
            CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();
            String email = userDetail.getEmail();

            UserDetails userDetails = userDetailService.loadUserByUsername(email);
            if(userDetails != null){
                Authentication newAuthentication = getAuthentication(userDetails);
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);


            }else{
                SecurityContextHolder.clearContext();
                log.error("UserValidationFilter can't find User INFO");
            }
        }

        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(UserDetails userDetails){
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
