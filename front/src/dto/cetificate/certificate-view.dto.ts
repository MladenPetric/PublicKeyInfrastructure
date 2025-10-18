export interface CertificateDTO {
  id: string; 
  serialNumber: string;
  organization: string;
  revoked: boolean;
  revocationReason: string;
  validFrom: string; 
  validTo: string;
  type: string; 
  publicKey: string;
  isCa: boolean;
}