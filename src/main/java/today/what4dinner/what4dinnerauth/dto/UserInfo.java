package today.what4dinner.what4dinnerauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {
    private String id;
    private String email;
    private String username;
    private String passwordHash;
    private boolean activated;
}
