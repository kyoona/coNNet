package houseInception.gptComm.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    public String createAccessToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);

        Calendar accessTokenCal = Calendar.getInstance();
        accessTokenCal.setTime(new Date());
        accessTokenCal.add(Calendar.WEEK_OF_MONTH, 1);
        Date accessTokenExpiresIn = accessTokenCal.getTime();

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);

        Calendar accessTokenCal = Calendar.getInstance();
        accessTokenCal.setTime(new Date());
        accessTokenCal.add(Calendar.MONTH, 1);
        Date accessTokenExpiresIn = accessTokenCal.getTime();

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.error("ExpiredJwtException: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("UnsupportedJwtException: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("MalformedJwtException: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("SignatureException: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
        return false;
    }
}
