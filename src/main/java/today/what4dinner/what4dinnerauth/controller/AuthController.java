package today.what4dinner.what4dinnerauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import today.what4dinner.what4dinnerauth.dto.UserInfo;
import today.what4dinner.what4dinnerauth.service.JWTService;
import today.what4dinner.what4dinnerauth.service.UserInfoService;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1")
public class AuthController {

    private final UserInfoService userInfoService;
    private final JWTService jwtService;

    public AuthController(UserInfoService userInfoService, JWTService jwtService) {
        this.userInfoService = userInfoService;
        this.jwtService = jwtService;
    }

    @PostMapping("/email-login")
    public ResponseEntity<Map<String, Object>> emailLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password) {

        Optional<UserInfo> userOpt = userInfoService.authenticate(email, password);
        if (userOpt.isPresent()) {
            UserInfo user = userOpt.get();
            String token = jwtService.generateToken(user.getId(), user.getEmail());
            return ResponseEntity.ok(Map.<String, Object>of(
                    "token", token,
                    "token_type", "Bearer",
                    "email", user.getEmail()
            ));
        }
        return ResponseEntity.status(401).body(Map.of(
                "error", "Invalid email or password"
        ));
    }

    @PostMapping("/email-register")
    public ResponseEntity<Map<String, Object>> emailRegister(
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        try {
            var user = userInfoService.register(email, username, password);
            return ResponseEntity.status(201).body(Map.of(
                    "email", user.getEmail(),
                    "username", user.getUsername()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Returns the caller's current profile, read fresh from the database on every call.
     * This is how clients get {@code familyId}: it is deliberately not a JWT claim, because
     * family membership can change while a 60-minute token is still valid and there is no way
     * to revoke an already-issued token.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal Jwt jwt) {
        Optional<UserInfo> userOpt = userInfoService.findById(jwt.getSubject());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        UserInfo user = userOpt.get();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("userId", user.getId());
        body.put("email", user.getEmail());
        body.put("username", user.getUsername());
        // Nullable in principle, and Map.of() rejects null values — hence LinkedHashMap.
        body.put("familyId", user.getFamilyId());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/exchange-code")
    public ResponseEntity<Map<String, Object>> exchangeJwtToken(
            @RequestParam("code") String code){
        Optional<String> newToken = jwtService.exchangeToken(code);
        if (newToken.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid code"));
        }
        return ResponseEntity.ok().body(
                Map.of("token", newToken.orElse(""))
        );
    }
}
