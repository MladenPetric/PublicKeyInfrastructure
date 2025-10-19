package rs.map.pki.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.map.pki.dto.CertificateDTO;
import rs.map.pki.model.Certificate;
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
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CertificateService {
    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${encryption.secret-key}")
    private String base64SecretKey;

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

    public byte[] generatePkcs12Keystore(UUID certificateId) throws Exception {
        Certificate certificate = certificateRepository.findById(certificateId).orElseThrow(() -> new IllegalArgumentException("Certificate not found"));

        String privateKeyPem = decryptPrivateKey(certificate.getPrivateKeyEncrypted());
        PrivateKey privateKey = loadPrivateKeyFromPem(privateKeyPem);

        X509Certificate x509Certificate = loadX509FromString(certificate.getPublicKey());

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);

        keyStore.setKeyEntry("certificate-key", privateKey, "changeit".toCharArray(), new java.security.cert.Certificate[]{x509Certificate});

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        keyStore.store(baos, "changeit".toCharArray());

        return baos.toByteArray();
    }

    private PrivateKey loadPrivateKeyFromPem(String pem) throws Exception {
        String privateKeyPEM = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");


        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    private X509Certificate loadX509FromString(String pem) throws Exception {
        String certPEM = pem
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(certPEM);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(decoded));
    }

    private String decryptPrivateKey(String encryptedKey) {
        // DEKRIPTOVANJA PRIVATNOG KLJUČA DODATI
        return encryptedKey;
    }


}
