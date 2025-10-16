package rs.map.pki.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
@Component
public class ActivationTokenGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();

    public String generateCode(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return base64UrlEncoder.encodeToString(randomBytes);
    }

    public byte[] generateBytes(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }
}
