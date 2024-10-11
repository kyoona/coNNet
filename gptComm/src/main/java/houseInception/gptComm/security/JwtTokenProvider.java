package houseInception.gptComm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long tokenValidTime; // 토큰 유효 시간 (밀리초 단위)


    public boolean validateToken(String token){
        Jwt<Header, Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJwt(token);
        return !claims.getBody().getExpiration().before(new Date());
    }
}
