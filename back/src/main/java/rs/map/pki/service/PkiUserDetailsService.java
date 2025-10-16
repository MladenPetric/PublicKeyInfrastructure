package rs.map.pki.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.map.pki.repository.UserRepository;
import rs.map.pki.util.PkiUserDetails;


@RequiredArgsConstructor
@Service
public class PkiUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return users.findByEmail(email)
                .map(PkiUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
