package rs.map.pki.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.map.pki.model.ActivationToken;
import rs.map.pki.model.User;
import rs.map.pki.repository.ActivationTokenRepository;
import rs.map.pki.repository.UserRepository;
import rs.map.pki.util.ActivationTokenGenerator;

import java.time.Instant;
import java.util.UUID;
@RequiredArgsConstructor
@Service
public class ActivationTokenService {
    private static final int VERIFICATION_CODE_LENGTH = 64;
    private final ActivationTokenRepository repo;
    private final UserRepository userRepository;
    private final ActivationTokenGenerator tokenGenerator;

    public String createToken(User user) {
        String token = tokenGenerator.generateCode(VERIFICATION_CODE_LENGTH);

        ActivationToken at = new ActivationToken();
        at.setToken(token);
        at.setUser(user);
        at.setExpiresAt(Instant.now().plusSeconds(24 * 60 * 60)); // 24h
        at.setUsed(false);

        repo.save(at);
        return token;
    }
}
