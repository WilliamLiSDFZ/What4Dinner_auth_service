package today.what4dinner.what4dinnerauth.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import today.what4dinner.what4dinnerauth.dto.UserInfo;
import today.what4dinner.what4dinnerauth.util.Uuids;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<UserInfo> userInfoRowMapper = new RowMapper<UserInfo>() {
        @Override
        public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserInfo(
                    rs.getString("id"),
                    rs.getString("email"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getBoolean("activated"),
                    rs.getString("family_id")
            );
        }
    };

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<UserInfo> findUserByEmail(String email) {
        List<UserInfo> results = jdbcTemplate.query(
                "SELECT id, email, username, password_hash, activated, family_id FROM users WHERE email = ?",
                userInfoRowMapper,
                email
        );
        return results.stream().findFirst();
    }

    @Override
    public Optional<UserInfo> findUserById(String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            // A `sub` that isn't a UUID cannot match any row, and binding it would make
            // Postgres reject the whole statement rather than simply return nothing.
            return Optional.empty();
        }
        List<UserInfo> results = jdbcTemplate.query(
                "SELECT id, email, username, password_hash, activated, family_id FROM users WHERE id = ?",
                userInfoRowMapper,
                uuid
        );
        return results.stream().findFirst();
    }

    @Override
    public String insertUser(String email, String username, String passwordHash, String familyId) {
        UUID id = Uuids.v7();
        // Both ids are bound as UUID objects, not Strings, so PgJDBC maps them to the native
        // uuid columns instead of trying a varchar -> uuid cast that Postgres rejects.
        jdbcTemplate.update(
                "INSERT INTO users (id, family_id, email, username, password_hash) VALUES (?, ?, ?, ?, ?)",
                id, UUID.fromString(familyId), email, username, passwordHash
        );
        return id.toString();
    }
}
