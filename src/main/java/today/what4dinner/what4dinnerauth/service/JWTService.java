package today.what4dinner.what4dinnerauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JWTService {

    private final JwtEncoder jwtEncoder;

    private final long expirationMinutes;

    public JWTService(JwtEncoder jwtEncoder,
                      @Value("${jwt.expiration-minutes:60}") long expirationMinutes) {
        this.jwtEncoder = jwtEncoder;
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
}
