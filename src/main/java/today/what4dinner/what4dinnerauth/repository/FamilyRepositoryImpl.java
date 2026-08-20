package today.what4dinner.what4dinnerauth.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import today.what4dinner.what4dinnerauth.util.Uuids;

import java.util.UUID;

@Repository
public class FamilyRepositoryImpl implements FamilyRepository {

    private final JdbcTemplate jdbcTemplate;

    public FamilyRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String insertFamily(String familyName) {
        UUID id = Uuids.v7();
        jdbcTemplate.update(
                "INSERT INTO family (id, family_name) VALUES (?, ?)",
                id, familyName
        );
        return id.toString();
    }
}
