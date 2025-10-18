package rs.map.pki.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/ca")
    ResponseEntity<?> adminRegisterUser(
            @RequestBody UserRegistrationRequestDto request
    ) {
        registrationService.registerCAUserByAdmin(request);
        return ResponseEntity.ok("Registracija uspešna!");
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activateAccount(@RequestParam("token") String token) {
        boolean activated = registrationService.activateAccount(token);
        if (activated) {
            return ResponseEntity.ok("Nalog uspešno aktiviran!");
        } else {
            return ResponseEntity.badRequest().body("Nevažeći ili istekao aktivacioni token.");
        }
    }


    // POST auth/activate?token=<activation_token>
    // GET auth/activate?token=<activation_token>

}
