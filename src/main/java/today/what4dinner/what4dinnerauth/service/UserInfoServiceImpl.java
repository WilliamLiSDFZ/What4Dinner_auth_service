package today.what4dinner.what4dinnerauth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import today.what4dinner.what4dinnerauth.dto.UserInfo;
import today.what4dinner.what4dinnerauth.repository.UserRepository;

import java.util.Optional;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserInfoServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserInfo> authenticate(String email, String rawPassword) {
        Optional<UserInfo> userOpt = userRepository.findUserByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPasswordHash())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public Optional<UserInfo> authenticateByGoogle(String email, String username) {
        Optional<UserInfo> userOpt = userRepository.findUserByEmail(email);
        if (userOpt.isPresent()) {
            return userOpt;
        }
        String id = userRepository.insertUser(email, username, null);
        return Optional.of(new UserInfo(id, email, username, null, false));
    }

    public UserInfo register(String email, String username, String rawPassword) {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        String hash = passwordEncoder.encode(rawPassword);
        String id = userRepository.insertUser(email, username, hash);
        return new UserInfo(id, email, username, hash, false);
    }
}
