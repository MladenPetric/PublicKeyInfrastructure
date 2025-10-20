package rs.map.pki.util;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.util.Date;


@Component
public class CertificateGeneratorUtil {

    public KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public X509Certificate generateCertificate(
            KeyPair keyPair,
            X500Name subject,
            X500Name issuer,
            PrivateKey issuerPrivateKey,
            Date startDate,
            Date endDate,
            boolean isCa
    ) throws Exception {

        BigInteger serialNumber = new BigInteger(64, new SecureRandom());
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serialNumber,
                startDate,
                endDate,
                subject,
                keyPair.getPublic()
        );

        // Ekstenzije
        if (isCa) {
            // Basic Constraints - CA = true
            certBuilder.addExtension(org.bouncycastle.asn1.x509.Extension.basicConstraints, true,
                    new org.bouncycastle.asn1.x509.BasicConstraints(true));
        }

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(issuerPrivateKey);

        X509CertificateHolder holder = certBuilder.build(signer);
        return new JcaX509CertificateConverter().getCertificate(holder);
    }
}
