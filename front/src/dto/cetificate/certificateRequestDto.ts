import { CertificateType } from './CertificateType';

export interface CertificateRequestDto {
  commonName: string; // CN
  organization: string; // O
  organizationalUnit: string; // OU
  country: string; // C
  email: string; // E
  validFrom: string;
  validTo: string;
  parentId?: string | null;
  issuerId?: string | null;
  type: CertificateType; // ROOT / INTERMEDIATE / END_ENTITY
  isCa: boolean; // da li je CA sertifikat
}
