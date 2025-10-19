package rs.map.pki.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.map.pki.dto.CertificateDTO;
import rs.map.pki.model.Certificate;
import rs.map.pki.model.RevocationReason;
import rs.map.pki.model.User;
import rs.map.pki.repository.CertificateRepository;
import rs.map.pki.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CertificateService {
    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    public Collection<CertificateDTO> getAllCertificates() {
        System.out.println("-----------------------------------");
        return certificateRepository.findAll()
                .stream()
                .map(CertificateDTO::new)
                .collect(Collectors.toList());
    }

    public Collection<CertificateDTO> getCertificatesByOwnerId(UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User with ID " + userId + " not found");
        }

        User user = userOpt.get();
        return certificateRepository.findByOwner(user)
                .stream()
                .map(CertificateDTO::new)
                .collect(Collectors.toList());
    }

    public Collection<CertificateDTO> getCertificatesByOrganization(String organization) {
        return certificateRepository.findByOrganization(organization)
                .stream()
                .map(CertificateDTO::new)
                .collect(Collectors.toList());
    }


    @Transactional
    public void revokeCertificate(UUID id, String reasonString) {
        Certificate cert = certificateRepository.findById(id).orElseThrow(() -> new RuntimeException("Certificate not found"));

        if (cert.isRevoked()) {
            throw new RuntimeException("Certificate already revoked");
        }

        RevocationReason reason;

        try {
            reason = RevocationReason.valueOf(reasonString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid revocation reason. Allowed reasons: " + Arrays.toString(RevocationReason.values()));
        }

        cert.setRevoked(true);
        cert.setRevocationReason(reason);

        certificateRepository.save(cert);
    }

    public List<CertificateDTO> getRevokedCertificates() {
        return certificateRepository.findAllByRevokedTrue()
                .stream()
                .map(CertificateDTO::new)
                .toList();
    }

}
