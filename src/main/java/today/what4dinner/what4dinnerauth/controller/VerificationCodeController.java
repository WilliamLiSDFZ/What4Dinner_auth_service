package today.what4dinner.what4dinnerauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/verification-code")
public class VerificationCodeController {

    @GetMapping("/email")
    public ResponseEntity<Map<String,Objects>> getEmailVerifCode(@RequestParam("email") String email) {


        return ResponseEntity.ok().body(null);
    }

}
