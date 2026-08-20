package today.what4dinner.what4dinnerauth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.what4dinner.what4dinnerauth.dto.UserInfo;
import today.what4dinner.what4dinnerauth.repository.FamilyRepository;
import today.what4dinner.what4dinnerauth.repository.UserRepository;

import java.util.Optional;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    /** Placeholder name every auto-created family starts with, until the user renames it. */
    private static final String DEFAULT_FAMILY_NAME = "default family name(please change)";

    private final UserRepository userRepository;

    private final FamilyRepository familyRepository;

    private final PasswordEncoder passwordEncoder;

    public UserInfoServiceImpl(UserRepository userRepository, FamilyRepository familyRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.familyRepository = familyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserInfo> authenticate(String email, String rawPassword) {
        Optional<UserInfo> userOpt = userRepository.findUserByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPasswordHash())) {
            return userOpt;
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<UserInfo> authenticateByGoogle(String email) {
        Optional<UserInfo> userOpt = userRepository.findUserByEmail(email);
        if (userOpt.isPresent()) {
            return userOpt;
        }
        String username = localPartOf(email);
        String familyId = familyRepository.insertFamily(DEFAULT_FAMILY_NAME);
        String id = userRepository.insertUser(email, username, null, familyId);
        return Optional.of(new UserInfo(id, email, username, null, false, familyId));
    }

    @Transactional
    public UserInfo register(String email, String username, String rawPassword) {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        String hash = passwordEncoder.encode(rawPassword);
        String familyId = familyRepository.insertFamily(DEFAULT_FAMILY_NAME);
        String id = userRepository.insertUser(email, username, hash, familyId);
        return new UserInfo(id, email, username, hash, false, familyId);
    }

    /**
     * Returns the part of an email address before the first {@code @}. Falls back to the
     * address itself when there is nothing usable in front of it, so callers never end up
     * writing null or an empty string into the non-null {@code users.username} column.
     */
    private static String localPartOf(String email) {
        if (email == null || email.isBlank()) {
            return email;
        }
        int at = email.indexOf('@');
        return at > 0 ? email.substring(0, at) : email;
    }
}
