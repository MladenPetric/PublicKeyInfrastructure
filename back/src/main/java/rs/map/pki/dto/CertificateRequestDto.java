package rs.map.pki.dto;

import lombok.Data;
import rs.map.pki.model.CertificateType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
public class CertificateRequestDto {
    private String commonName;  // CN
    private String organization;  // O
    private String organizationalUnit;  // OU
    private String country;  // C
    private String email;  // E
    private LocalDate validFrom;
    private LocalDate validTo;
    private UUID parentId;
    private CertificateType type; // ROOT / INTERMEDIATE / END_ENTITY
    private UUID issuerId; // ko potpisuje (CA)
    private boolean isCa;
}
