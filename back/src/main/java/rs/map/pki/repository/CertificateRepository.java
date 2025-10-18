package rs.map.pki.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.map.pki.model.Certificate;
import rs.map.pki.model.User;

import java.util.Collection;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

    Collection<Certificate> findByOwner(User owner);

    Collection<Certificate> findByOrganization(String organization);
}
