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
  showRevokeDialog = false;
  selectedCertId: string | null = null;
  selectedReason: string = '';

  revocationReasons: string[] = [
    'UNSPECIFIED',
    'KEY_COMPROMISE',
    'CA_COMPROMISE',
    'AFFILIATION_CHANGED',
    'SUPERSEDED',
    'CESSATION_OF_OPERATION',
    'CERTIFICATE_HOLD',
    'REMOVE_FROM_CRL',
    'PRIVILEGE_WITHDRAWN',
    'AA_COMPROMISE'
  ];

  constructor(private certificateService: CertificateService) {}

  ngOnInit(): void {

    this.userRole = localStorage.getItem('userRole');
    this.userId = localStorage.getItem('userId'); 
    this.organization = localStorage.getItem('organization');                                                                      
    this.loadCertifiactes()
  }

  loadCertifiactes(): void {
    if (this.userRole === 'ADMIN') {
      this.loadAllCertificates();
    } else if (this.userRole === 'USER' && this.userId) {
      this.loadCertificatesByOwner(this.userId);
    } else if (this.userRole === 'ORG' && this.organization) {
      this.loadCertificatesByOrganization(this.organization);
    } else {
      console.warn('Nedostaju podaci za ulogovanog korisnika.');
    }
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
    this.certificateService.downloadCertificate(certId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'certificate.p12';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Error downloading certificate:', err)
    });
  }

  onRevoke(certId: string): void {
    this.selectedCertId = certId;
    this.showRevokeDialog = true;
  }

  confirmRevoke(): void {
    if (!this.selectedCertId || !this.selectedReason) {
      alert('Please select a reason before confirming.');
      return;
    }

    this.certificateService.revokeCertificate(this.selectedCertId, this.selectedReason).subscribe({
      next: () => {
        alert('Certificate revoked successfully.');
        this.showRevokeDialog = false;
        this.selectedReason = '';
        this.selectedCertId = null;
        this.loadCertifiactes();
      },
      error: (err) => {
        alert('Error revoking certificate.');
        console.error(err);
      }
    });
  }

  cancelRevoke(): void {
    this.showRevokeDialog = false;
    this.selectedReason = '';
    this.selectedCertId = null;
  }
}
