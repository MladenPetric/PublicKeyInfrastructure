package rs.map.pki.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.map.pki.model.ActivationToken;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, UUID> {
    Optional<ActivationToken> findByToken(String token);
}
