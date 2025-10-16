package rs.map.pki.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.map.pki.dto.UserRegistrationRequestDto;
import rs.map.pki.service.RegistrationService;
@RequiredArgsConstructor
@RestController
@RequestMapping(path = {"/auth"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class RegistrationController {
    private final RegistrationService registrationService;
    // POST auth/register
    @PostMapping( "/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequestDto request) {
        registrationService.registerUser(request);
        return ResponseEntity.ok("Registracija uspešna! Proverite mejl za aktivacioni link.");
    }
    // POST admin@auth/register/<role>
    @PostMapping("/register/{role:^(admin|ca|simpson)$}")
    ResponseEntity<?> adminRegisterUser(
            @PathVariable String role,
            Object data
    ) {
        return null;
    }

    // POST auth/activate?token=<activation_token>
    // GET auth/activate?token=<activation_token>

}
