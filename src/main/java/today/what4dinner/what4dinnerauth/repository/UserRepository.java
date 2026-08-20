package today.what4dinner.what4dinnerauth.repository;

import today.what4dinner.what4dinnerauth.dto.UserInfo;

import java.util.Optional;

/**
 * Repository for user data persistence.
 */
public interface UserRepository {

    /**
     * Retrieves a user from the database based on the provided email.
     *
     * @param email the email address of the user to retrieve
     * @return an Optional containing the UserInfo if a user with the specified email exists,
     *         or an empty Optional if no such user is found
     */
    Optional<UserInfo> findUserByEmail(String email);

    /**
     * Inserts a new user record into the database with the provided email, username, hashed
     * password and owning family.
     *
     * @param email the email address of the user to be added
     * @param username the username of the user to be added
     * @param passwordHash the hashed password of the user to be added
     * @param familyId the ID of the family the user belongs to
     * @return the unique identifier (ID) of the newly inserted user
     */
    String insertUser(String email, String username, String passwordHash, String familyId);
}
