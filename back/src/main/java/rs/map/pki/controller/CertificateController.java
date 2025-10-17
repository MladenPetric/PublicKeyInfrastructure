package rs.map.pki.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.map.pki.dto.CertificateDTO;
import rs.map.pki.model.User;
import rs.map.pki.service.CertificateService;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping(path = {"/certificate"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping(value = "/get-all")
    public ResponseEntity<Collection<CertificateDTO>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    @GetMapping("/get-by-owner/{userId}")
    public ResponseEntity<Collection<CertificateDTO>> getCertificatesByOwnerId(@PathVariable UUID userId) {
        return ResponseEntity.ok(certificateService.getCertificatesByOwnerId(userId));
    }

    @GetMapping(value = "/get-by-organization/{organization}")
    public ResponseEntity<Collection<CertificateDTO>> getCertificatesByOrganization(@PathVariable String organization) {
        return ResponseEntity.ok(certificateService.getCertificatesByOrganization(organization));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable UUID id) {
        try {
            byte[] keystoreData = certificateService.generatePkcs12Keystore(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "certificate.p12");

            return new ResponseEntity<>(keystoreData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}
