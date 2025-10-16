package rs.map.pki.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.map.pki.model.User;
import rs.map.pki.repository.UserRepository;

import java.util.List;

//@Profile()
@RequiredArgsConstructor
@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        var users = List.of(
                new User(null, "admin@map.rs", passwordEncoder.encode("securepassword"),"Admin", "Adminović", "MAP", User.Role.ROLE_ADMIN)
        );

        this.users.saveAllAndFlush(users);
    }
}
