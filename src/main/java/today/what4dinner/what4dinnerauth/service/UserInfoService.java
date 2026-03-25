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
     * a new user is created automatically.
     *
     * @param email    the email address from the Google profile
     * @param username the display name from the Google profile
     * @return an {@link Optional} containing the existing or newly created {@link UserInfo}
     */
    Optional<UserInfo> authenticateByGoogle(String email, String username);

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
