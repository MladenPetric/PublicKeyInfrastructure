package rs.map.pki.service;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.map.pki.dto.CertificateDTO;
import rs.map.pki.dto.CertificateRequestDto;
import rs.map.pki.model.Certificate;
import rs.map.pki.model.PkiX500Name;
import rs.map.pki.model.RevocationReason;
import rs.map.pki.model.User;
import rs.map.pki.repository.CertificateRepository;
import rs.map.pki.repository.UserRepository;
import rs.map.pki.util.CertificateGeneratorUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CertificateService {
    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CertificateGeneratorUtil certificateGeneratorUtil;

    @Value("${app.master-key}")
    private String masterKey;

    @Value("${encryption.secret-key}")
    private String base64SecretKey;

    public void generateCertificate(CertificateRequestDto request) {
        try {
            // Generiši par ključeva
            KeyPair keyPair = certificateGeneratorUtil.generateKeyPair();

            X500Name subject = new X500NameBuilder(BCStyle.INSTANCE)
                    .addRDN(BCStyle.CN, request.getCommonName())
                    .addRDN(BCStyle.O, request.getOrganization())
                    .addRDN(BCStyle.OU, request.getOrganizationalUnit())
                    .addRDN(BCStyle.C, request.getCountry())
                    .addRDN(BCStyle.E, request.getEmail())
                    .build();
            X500Name issuer;
            if (request.getIssuerId() == null) {
                issuer = subject;
            }else{
                Certificate issuerCert = certificateRepository.findById(request.getIssuerId()).orElseThrow();
                var issuerData = issuerCert.getSubject();
                issuer = issuerData.toX500Name();
            }
            Certificate parent = null;
            if (request.getParentId() != null) {
                parent = certificateRepository.findById(request.getParentId()).orElseThrow();
            }
            // Pozovi util klasu da napravi sertifikat
            Certificate cert = new Certificate();
            cert.setSerialNumber(UUID.randomUUID().toString());
            cert.setSubject(new PkiX500Name(subject));
            cert.setIssuer(new PkiX500Name(issuer));
            cert.setValidFrom(request.getValidFrom());
            cert.setValidTo(request.getValidTo());
            cert.setCa(request.isCa());
            cert.setParent(parent);
            cert.setRevoked(false);
            cert.setType(request.getType());

            String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKeyEncrypted = encryptPrivateKey(keyPair.getPrivate(), masterKey);

            cert.setPublicKey(publicKeyBase64);
            cert.setPrivateKeyEncrypted(privateKeyEncrypted);
            // Sačuvaj u bazi
            certificateRepository.save(cert);

            // (Kasnije) – sačuvaj u bazu / fajl / keystore
            // certificateRepository.save(...);
        } catch (Exception e) {
            throw new RuntimeException("Greška pri generisanju sertifikata: " + e.getMessage(), e);
        }
    }

    private String encryptPrivateKey(PrivateKey privateKey, String masterKey) throws Exception {
        byte[] key = normalizeKey(masterKey);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(privateKey.getEncoded());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public PrivateKey decryptPrivateKey(String encryptedKeyBase64) throws Exception {
        byte[] key = normalizeKey(masterKey);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedKeyBase64));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decrypted));
    }

    private byte[] normalizeKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length == 16) {
            return keyBytes;
        } else if (keyBytes.length < 16) {
            // dopuni nulama ako je kraći
            return Arrays.copyOf(keyBytes, 16);
        } else {
            // skrati ako je duži
            return Arrays.copyOf(keyBytes, 16);
        }
    }

    public Collection<CertificateDTO> getAllCaCertificates() {
        return certificateRepository.findAll()
                .stream()
                .filter(Certificate::isCa)  // uzmi samo gde je isCa == true
                .map(CertificateDTO::new)
                .collect(Collectors.toList());
    }

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



    public byte[] generatePkcs12Keystore(UUID certificateId) throws Exception {
        Certificate certEntity = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found"));

        PrivateKey privateKey = decryptPrivateKey(certEntity.getPrivateKeyEncrypted());

        byte[] publicKeyBytes = Base64.getDecoder().decode(certEntity.getPublicKey());
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);

        X500Name subject = certEntity.getSubject().toX500Name();
        X500Name issuer = certEntity.getIssuer().toX500Name();

        X509Certificate x509Certificate = certificateGeneratorUtil.generateCertificate(
                new KeyPair(publicKey, privateKey),
                subject,
                issuer,
                privateKey, // potpisuje sam sebe ako nema parent
                java.sql.Date.valueOf(certEntity.getValidFrom()),
                java.sql.Date.valueOf(certEntity.getValidTo()),
                certEntity.isCa()
        );

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);

        keyStore.setKeyEntry(
                "certificate-key",
                privateKey,
                "changeit".toCharArray(),
                new java.security.cert.Certificate[]{x509Certificate}
        );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            keyStore.store(baos, "changeit".toCharArray());
            return baos.toByteArray();
        }
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

}
