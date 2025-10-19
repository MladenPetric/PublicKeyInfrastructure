package rs.map.pki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.map.pki.model.Certificate;
import rs.map.pki.model.CertificateType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDTO {
    private UUID id;
    private String serialNumber;
    private String organization;
    private boolean revoked;
    private String revocationReason;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private CertificateType type;
    private String publicKey;
    private boolean isCa;

    public CertificateDTO(Certificate c) {
        this.id = c.getId();
        this.serialNumber = c.getSerialNumber();
        this.organization = c.getOrganization();
        this.revoked = c.isRevoked();
        this.revocationReason = String.valueOf(c.getRevocationReason());
        this.validFrom = c.getValidFrom();
        this.validTo = c.getValidTo();
        this.type = c.getType();
        this.publicKey = c.getPublicKey();
        this.isCa = c.isCa();
    }
}
