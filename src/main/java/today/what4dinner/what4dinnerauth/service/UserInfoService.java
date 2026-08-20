package today.what4dinner.what4dinnerauth.service;

import today.what4dinner.what4dinnerauth.dto.UserInfo;

import java.util.Optional;

/**
 * Service for user authentication and registration.
 */
public interface UserInfoService {

    /**
     * Authenticates a user by verifying the provided email and password.
     *
     * @param email       the email address of the user
     * @param rawPassword the plain-text password to verify
     * @return an {@link Optional} containing the {@link UserInfo} if credentials are valid,
     *         or empty if authentication fails
     */
    Optional<UserInfo> authenticate(String email, String rawPassword);

    /**
     * Authenticates a user via Google OAuth2. If no account exists for the given email,
     * a new user is created automatically with its username derived from the local part
     * of the email address (everything before the {@code @}).
     *
     * @param email the email address from the Google profile
     * @return an {@link Optional} containing the existing or newly created {@link UserInfo}
     */
    Optional<UserInfo> authenticateByGoogle(String email);

    /**
     * Looks up the current state of a user by id. Used to serve profile data — notably
     * {@code familyId} — that is deliberately kept out of the JWT because it can change while
     * a token is still valid.
     *
     * @param userId the unique identifier of the user, i.e. the token's {@code sub} claim
     * @return an {@link Optional} containing the user, or empty if no such user exists
     */
    Optional<UserInfo> findById(String userId);

    /**
     * Registers a new user with email and password credentials.
     *
     * @param email       the email address for the new account
     * @param username    the display name for the new account
     * @param rawPassword the plain-text password (will be hashed before storage)
     * @return the newly created {@link UserInfo}
     * @throws IllegalArgumentException if the email is already registered
     */
    UserInfo register(String email, String username, String rawPassword);
}
