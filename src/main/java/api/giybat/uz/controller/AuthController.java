package api.giybat.uz.controller;

import api.giybat.uz.dto.AuthorizationDTO;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.RegistrationDTO;
import api.giybat.uz.dto.SmsVerificationDTO;
import api.giybat.uz.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<String> registration(@Valid @RequestBody RegistrationDTO dto) {
        return ResponseEntity.ok(authService.registration(dto));
    }

    @GetMapping("/email-verification/{token}")
    public ResponseEntity<String> registration(@PathVariable("token") String token) {
        return ResponseEntity.ok(authService.emailVerification(token));
    }

    @PutMapping("/sms-verification")
    public ResponseEntity<String> verificationBySms(@RequestBody SmsVerificationDTO dto) {
        return ResponseEntity.ok(authService.smsVerification(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<ProfileDTO> login(@Valid @RequestBody AuthorizationDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
