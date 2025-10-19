package rs.map.pki.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.map.pki.model.Certificate;
import rs.map.pki.model.CertificateType;
import rs.map.pki.model.User;
import rs.map.pki.repository.CertificateRepository;
import rs.map.pki.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//@Profile()

@RequiredArgsConstructor
@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final CertificateRepository certificates;


    @Override
    public void run(String... args) throws Exception {
//        var users = List.of(
//                new User((UUID) null, "admin@map.rs", passwordEncoder.encode("securepassword"),"Admin", "Adminović", "MAP", User.Role.ROLE_ADMIN, User.Status.ACTIVE),
//                new User(null, "organizer@map.rs", passwordEncoder.encode("organizerpass"), "Olga", "Olgic", "MAP", User.Role.ROLE_CA, User.Status.ACTIVE),
//                new User(null, "user@map.rs", passwordEncoder.encode("userpass"), "Uroš", "Urosevic", "MAP", User.Role.ROLE_SIMPSON, User.Status.ACTIVE)
//        );
//
//        this.users.saveAllAndFlush(users);
//
//        var admin = users.get(0);
//        var ca  = users.get(1);
//        var user = users.get(2);
//
//        var certificates = List.of(
//                new Certificate(null, "SN-001", "ORG", ca, false, "", LocalDateTime.now(), LocalDateTime.now().plusYears(1), null, CertificateType.ROOT, "dummy-public-key-1", "dummy-private-key-1", "dummy-signature", true),
//                new Certificate(null, "SN-002", "ORG", user, false, "", LocalDateTime.now(), LocalDateTime.now().plusYears(1), null, CertificateType.INTERMEDIATE, "dummy-public-key-2", "dummy-private-key-2", "dummy-signature", true),
//                new Certificate(null, "SN-003", "MAP", user, false, "", LocalDateTime.now(), LocalDateTime.now().plusYears(1), null, CertificateType.END_ENTITY, "dummy-public-key-3", "dummy-private-key-3", "dummy-signature", false)
//        );
//
//        this.certificates.saveAllAndFlush(certificates);


    }
}
