package today.what4dinner.what4dinnerauth.service;

import java.util.Optional;

/**
 * Service for generating and validating JSON Web Tokens (JWT) used for authentication.
 *
 * <p>Tokens carry identity only — {@code sub}, {@code email}, {@code iss}. A user's
 * {@code family_id} is deliberately <strong>not</strong> a claim: family membership can change,
 * and a token is valid for 60 minutes with no way to revoke it, so a baked-in family id would
 * let a user keep reading and writing the family they just left for the rest of that hour.
 * Consumers read the current family from {@code users.family_id} themselves — a primary-key lookup
 * on the shared database.
 */
public interface JWTService {

    /**
     * Generates a JWT with the configured default expiration.
     *
     * @param userId the unique identifier of the user (stored as the {@code sub} claim)
     * @param email  the email address of the user (stored as the {@code email} claim)
     * @return the encoded JWT string
     */
    String generateToken(String userId, String email);

    /**
     * Generates a short-lived JWT with a 15-minute expiration, typically used as an
     * OAuth2 redirect code that is exchanged for a full-duration token.
     *
     * @param userId the unique identifier of the user (stored as the {@code sub} claim)
     * @param email  the email address of the user (stored as the {@code email} claim)
     * @return the encoded JWT string
     */
    String generateShortTermToken(String userId, String email);

    /**
     * Validates the given token and issues a new full-duration token with the same claims.
     *
     * @param token the JWT string to validate and exchange
     * @return an {@link Optional} containing the new token, or empty if validation fails
     */
    Optional<String> exchangeToken(String token);
}
