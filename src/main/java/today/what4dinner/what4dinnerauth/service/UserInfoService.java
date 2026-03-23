package today.what4dinner.what4dinnerauth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import today.what4dinner.what4dinnerauth.dto.UserInfo;
import today.what4dinner.what4dinnerauth.repository.MysqlRepository;

import java.util.Optional;

@Service
public class UserInfoService {

    private final MysqlRepository mysqlRepository;

    private final PasswordEncoder passwordEncoder;

    public UserInfoService(MysqlRepository mysqlRepository, PasswordEncoder passwordEncoder) {
        this.mysqlRepository = mysqlRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserInfo> authenticate(String email, String rawPassword) {
        Optional<UserInfo> userOpt = mysqlRepository.findUserByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPasswordHash())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public UserInfo register(String email, String username, String rawPassword) {
        if (mysqlRepository.findUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        String hash = passwordEncoder.encode(rawPassword);
        String id = mysqlRepository.insertUser(email, username, hash);
        return new UserInfo(id, email, username, hash, false);
    }
}
