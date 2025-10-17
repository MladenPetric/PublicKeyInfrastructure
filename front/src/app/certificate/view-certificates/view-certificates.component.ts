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
    // TODO: implementirati API poziv kasnije
  }

  onRevoke(certId: string): void {
    const reason = prompt('Enter revocation reason (e.g. keyCompromise, cessationOfOperation, affiliationChanged):');
  
    if (!reason) {
      alert('Revocation cancelled.');
      return;
    }

    this.certificateService.revokeCertificate(certId, reason).subscribe({
      next: () => {
        alert('Certificate revoked successfully.');
        this.loadCertifiactes(); 
      },
      error: (err) => {
        console.error('Error revoking certificate', err);
        alert('Error revoking certificate.');
      }
      });
    }
}
