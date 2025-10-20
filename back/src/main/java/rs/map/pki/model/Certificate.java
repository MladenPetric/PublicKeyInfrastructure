package rs.map.pki.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String serialNumber;

    @Embedded
    @AttributeOverride(name = "dn", column = @Column(name = "subject_dn", nullable = false))
    private PkiX500Name subject;

    @Embedded
    @AttributeOverride(name = "dn", column = @Column(name = "issuer_dn", nullable = false))
    private PkiX500Name issuer;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @Column(nullable = false)
    private boolean revoked;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private RevocationReason revocationReason;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validTo;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Certificate parent;

    @Enumerated(EnumType.STRING)
    private CertificateType type;

    @Column(columnDefinition = "TEXT")
    private String publicKey;

    @Column(columnDefinition = "TEXT")
    private String privateKeyEncrypted;

    private boolean isCa;
}

