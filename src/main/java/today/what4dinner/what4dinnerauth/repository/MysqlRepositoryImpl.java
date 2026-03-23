package today.what4dinner.what4dinnerauth.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import today.what4dinner.what4dinnerauth.dto.UserInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MysqlRepositoryImpl implements MysqlRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<UserInfo> userInfoRowMapper = new RowMapper<UserInfo>() {
        @Override
        public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserInfo(
                    rs.getString("id"),
                    rs.getString("email"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getBoolean("activated")
            );
        }
    };

    public MysqlRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<UserInfo> findUserByEmail(String email) {
        List<UserInfo> results = jdbcTemplate.query(
                "SELECT id, email, username, password_hash, activated FROM users WHERE email = ?",
                userInfoRowMapper,
                email
        );
        return results.stream().findFirst();
    }

    @Override
    public String insertUser(String email, String username, String passwordHash) {
        String id = UUID.randomUUID().toString();
        jdbcTemplate.update(
                "INSERT INTO users (id, email, username, password_hash) VALUES (?, ?, ?, ?)",
                id, email, username, passwordHash
        );
        return id;
    }
}
