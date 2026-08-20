package today.what4dinner.what4dinnerauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import today.what4dinner.what4dinnerauth.dto.UserInfo;
import today.what4dinner.what4dinnerauth.service.JWTService;
import today.what4dinner.what4dinnerauth.service.UserInfoService;

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
