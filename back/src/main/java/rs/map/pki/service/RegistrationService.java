package rs.map.pki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.map.pki.dto.UserRegistrationRequestDto;
import rs.map.pki.model.User;
import rs.map.pki.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivationTokenService activationTokenService;

    @Transactional
    public void registerUser(UserRegistrationRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Korisnik sa ovom email adresom već postoji.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getFirstName());
        user.setSurname(request.getLastName());
        user.setOrganization(request.getOrganization());
        user.setRole(User.Role.ROLE_SIMPSON);
        user.setStatus(User.Status.INACTIVE);

        userRepository.save(user);

        // generiši aktivacioni token
        String token = activationTokenService.createToken(user);

        // pošalji aktivacioni mejl
        //emailService.sendActivationEmail(user.getEmail(), token);
    }




}
