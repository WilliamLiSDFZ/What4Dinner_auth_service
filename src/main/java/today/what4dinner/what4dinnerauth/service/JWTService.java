package today.what4dinner.what4dinnerauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class JWTService {

    private final JwtEncoder jwtEncoder;

    private final JwtDecoder jwtDecoder;

    private final long expirationMinutes;

    public JWTService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(String userId, String email) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("what4dinner-auth")
                .subject(userId)
                .claim("email", email)
                .issuedAt(now)
                .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateShortTermToken(String userId, String email) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("what4dinner-auth")
                .subject(userId)
                .claim("email", email)
                .issuedAt(now)
                .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String exchnageToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        Map<String, Object> claims = jwt.getClaims();
        String userId = (String) claims.get("sub");
        String email = (String) claims.get("email");
        return generateToken(userId, email);
    }
}
