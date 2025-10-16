package rs.map.pki.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/auth"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class RegistrationController {

    // POST auth/register

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
