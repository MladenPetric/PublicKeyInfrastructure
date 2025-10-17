import { Component } from '@angular/core';
import { CertificateDTO } from '../../../dto/cetificate/certificate-view.dto';
import { CertificateService } from '../../../services/cetificate.service';

@Component({
  selector: 'app-view-certificates',
  standalone: false,
  templateUrl: './view-certificates.component.html',
  styleUrl: './view-certificates.component.css'
})
export class ViewCertificatesComponent {
  certificates: CertificateDTO[] = [];
  userRole: string | null = null;
  userId: string | null = null;
  organization: string | null = null;

  constructor(private certificateService: CertificateService) {}

  ngOnInit(): void {

    this.userRole = localStorage.getItem('userRole');
    this.userId = localStorage.getItem('userId'); 
    this.organization = localStorage.getItem('organization');

     if (this.userRole === 'ADMIN') {
      this.loadAllCertificates();
    } else if (this.userRole === 'USER' && this.userId) {
      this.loadCertificatesByOwner(this.userId);
    } else if (this.userRole === 'ORG' && this.organization) {
      this.loadCertificatesByOrganization(this.organization);
    } else {
      console.warn('Nedostaju podaci za ulogovanog korisnika.');
    }

  // 👇 Za sada statički primeri — kasnije ćeš ovo puniti iz servisa
  //   this.certificates = [
  //     {
  //       id: '1',
  //       serialNumber: 'ABC123',
  //       organization: 'MAP Security',
  //       revoked: false,
  //       revocationReason: '',
  //       validFrom: '2024-01-01T00:00:00',
  //       validTo: '2026-01-01T00:00:00',
  //       type: 'ROOT',
  //       publicKey: 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp...',
  //       isCa: true
  //     },
  //     {
  //       id: '2',
  //       serialNumber: 'XYZ789',
  //       organization: 'TechTrust',
  //       revoked: true,
  //       revocationReason: 'Compromised key',
  //       validFrom: '2023-03-01T00:00:00',
  //       validTo: '2025-03-01T00:00:00',
  //       type: 'END_ENTITY',
  //       publicKey: 'MIIBCgKCAQEAtqksnZ2bK7dY7...',
  //       isCa: false
  //     }
  //   ];
  }

  loadAllCertificates(): void {
    this.certificateService.getAllCertificates().subscribe({
      next: (data) => this.certificates = data,
      error: (err) => console.error('Error fetching all certificates', err)
    });
  }

  loadCertificatesByOwner(userId: string): void {
    this.certificateService.getCertificatesByOwnerId(userId).subscribe({
      next: (data) => this.certificates = data,
      error: (err) => console.error('Error fetching certificates by owner', err)
    });
  }

  loadCertificatesByOrganization(org: string): void {
    this.certificateService.getCertificatesByOrganization(org).subscribe({
      next: (data) => this.certificates = data,
      error: (err) => console.error('Error fetching certificates by organization', err)
    });
  }

  onDownload(certId: string): void {
    // TODO: implementirati API poziv kasnije
  }

  onRevoke(certId: string): void {
    // TODO: implementirati API poziv kasnije
  }
}
