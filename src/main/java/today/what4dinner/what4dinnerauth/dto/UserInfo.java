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
    // Last in the field list on purpose: @AllArgsConstructor is positional and familyId is another
    // String, so keeping it apart from `id` means a swapped argument fails to compile.
    private String familyId;
}
