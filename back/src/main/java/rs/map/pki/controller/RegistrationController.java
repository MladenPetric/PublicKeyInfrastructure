package rs.map.pki.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import rs.map.pki.config.FrontProperties;
import rs.map.pki.dto.UserRegistrationRequestDto;
import rs.map.pki.service.RegistrationService;
@RequiredArgsConstructor
@RestController
@RequestMapping(path = {"/api/auth"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class RegistrationController {
    private final RegistrationService registrationService;
    // POST auth/register
    @PostMapping( "/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequestDto request) {
        registrationService.registerUser(request);
        return ResponseEntity.ok("Registration successful! Check email for activation link.");
    }
    // POST admin@auth/register/<role>
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/ca")
    ResponseEntity<?> adminRegisterUser(
            @RequestBody UserRegistrationRequestDto request
    ) {
        registrationService.registerCAUserByAdmin(request);
        return ResponseEntity.ok("Registration successful!");
    }

    @GetMapping("/activate")
    public RedirectView activateAccount(@RequestParam("token") String token, FrontProperties frontProperties) {
        boolean activated = registrationService.activateAccount(token);
        if (activated) {
            return new RedirectView("http://localhost:4200/login");
        } else {
            return new RedirectView("http://localhost:4200/register");
        }
    }


    // POST auth/activate?token=<activation_token>
    // GET auth/activate?token=<activation_token>

}
