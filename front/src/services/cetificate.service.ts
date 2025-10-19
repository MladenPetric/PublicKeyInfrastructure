import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CertificateDTO } from '../dto/cetificate/certificate-view.dto';


@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  private apiUrl = 'https://localhost:8080/certificate'; 

  constructor(private http: HttpClient) {}

  getAllCertificates(): Observable<CertificateDTO[]> {
    return this.http.get<CertificateDTO[]>(`${this.apiUrl}/get-all`);
  }

  getCertificatesByOwnerId(userId: string): Observable<CertificateDTO[]> {
    return this.http.get<CertificateDTO[]>(`${this.apiUrl}/get-by-owner/${userId}`);
  }

  getCertificatesByOrganization(org: string): Observable<CertificateDTO[]> {
    return this.http.get<CertificateDTO[]>(`${this.apiUrl}/get-by-organization/${org}`);
  }





 

  revokeCertificate(id: string, reason: string): Observable<void> {
    const body = { reason };
    return this.http.put<void>(`${this.apiUrl}/revoke/${id}`, body);
  }
  
  getRevokedCertificates(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/crl`);
  }
}