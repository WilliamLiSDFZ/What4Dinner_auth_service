package today.what4dinner.what4dinnerauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class JWTServiceImpl implements JWTService {

    private final JwtEncoder jwtEncoder;

    private final JwtDecoder jwtDecoder;

    private final long expirationMinutes;

    public JWTServiceImpl(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(String userId, String email) {
        return encode(userId, email, expirationMinutes);
    }

    public String generateShortTermToken(String userId, String email) {
        return encode(userId, email, 15);
    }

    public Optional<String> exchangeToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return Optional.of(generateToken(jwt.getSubject(), jwt.getClaimAsString("email")));
    }

    private String encode(String userId, String email, long ttlMinutes) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("what4dinner-auth")
                .subject(userId)
                .issuedAt(now)
                .expiresAt(now.plus(ttlMinutes, ChronoUnit.MINUTES));
        // JwtClaimsSet.Builder.claim() asserts the value is non-null, so an optional claim has
        // to be guarded rather than passed straight through.
        if (email != null) {
            claims.claim("email", email);
        }
        return jwtEncoder.encode(JwtEncoderParameters.from(claims.build())).getTokenValue();
    }
}
