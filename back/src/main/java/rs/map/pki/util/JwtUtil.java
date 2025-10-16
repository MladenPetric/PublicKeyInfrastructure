package rs.map.pki.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;
import rs.map.pki.config.JwtProperties;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;


@Component
public class JwtUtil {

    private final SecretKey signingKey;
    private final JwtProperties jwtProperties;


    @Autowired
    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        var secret = Hex.decode(jwtProperties.getSecret());
        signingKey = Keys.hmacShaKeyFor(secret);
    }


    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, jwtProperties.getAccessExpiration());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, jwtProperties.getRefreshExpiration());
    }

    private String generateToken(UserDetails userDetails, Duration expiration) {
        var now = new Date();
        var notAfter = new Date(now.getTime() + expiration.toMillis());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .issuedAt(now)
                .expiration(notAfter)
                .signWith(signingKey)
                .compact();
    }

    @Nullable
    public String extractUsername(String jwt) {
        try {
            return extractClaims(jwt).getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public Date extractExpiration(String jwt) {
        try {
            return extractClaims(jwt).getExpiration();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isTokenValid(String jwt) {
        try {
            var claims = extractClaims(jwt);
            return claims.getExpiration() != null && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

}
