import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CertificateService } from '../../../services/cetificate.service';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-create-certifcates',
  standalone: false,
  templateUrl: './create-certifcates.component.html',
  styleUrl: './create-certifcates.component.css',
})
export class CreateCertifcatesComponent {
  certificateForm!: FormGroup;
  availableCAs: any[] = [];
  issuers: any[] = [];
  userRole: string | undefined;

  constructor(
    private fb: FormBuilder,
    private certificateService: CertificateService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Praćenje uloge korisnika
    this.authService.user$.subscribe({
      next: (data) => (this.userRole = data?.role),
    });

    // Definišemo osnovnu formu
    this.certificateForm = this.fb.group({
      type: ['END_ENTITY', Validators.required],
      organization: ['', Validators.required],
      ownerId: ['', Validators.required],
      validFrom: ['', Validators.required],
      validTo: ['', Validators.required],
      parentId: [''],
      isCa: [false],
      publicKey: ['', Validators.required],
      privateKeyEncrypted: ['', Validators.required],
      digitalSignature: ['', Validators.required],
      issuerId: [''],
      organizationUnit: [''],
      country: [''],
      commonName: [''],
    });
    this.certificateForm.get('type')?.valueChanges.subscribe((type) => {
      if (type === 'ROOT') {
        // Ako je root, nema parenta ni issuer-a
        this.certificateForm.patchValue({
          parentId: '',
          issuerId: '',
          isCa: true, // root je uvek CA
        });

        this.certificateForm.get('parentId')?.disable();
        this.certificateForm.get('issuerId')?.disable();
      } else {
        // Ako nije root, omogućavamo izbor
        this.certificateForm.get('parentId')?.enable();
        this.certificateForm.get('issuerId')?.enable();
      }
    });

    this.loadAvailableCAs();
    this.loadIssuers();
  }

  loadAvailableCAs(): void {
    this.certificateService.getAllCaCertificates().subscribe({
      next: (data) => (this.availableCAs = data),
      error: (err) => console.error('Failed to load CA certificates', err),
    });
  }

  loadIssuers(): void {
    this.certificateService.getAllCertificates().subscribe({
      next: (data) => (this.issuers = data),
      error: (err) => console.error('Failed to load CA certificates', err),
    });
  }

  createCertificate(): void {
    //   if (this.certificateForm.invalid) {
    //     this.certificateForm.markAllAsTouched();
    //     return;
    //   }
    //   const payload = this.certificateForm.value;
    //   this.certificateService.createCertificate(payload).subscribe({
    //     next: (response) => {
    //       alert('Certificate successfully created!');
    //       this.certificateForm.reset();
    //     },
    //     error: (err) => {
    //       console.error('Error creating certificate', err);
    //       alert('Failed to create certificate.');
    //     }
    //   });

    // if (this.certificateForm.invalid) {
    //   console.log('USAO');
    //   this.certificateForm.markAllAsTouched();
    //   return;
    // }

    const formValue = this.certificateForm.value;

    // Pripremamo payload koji odgovara backend DTO-u
    const payload = {
      commonName: formValue.commonName,
      organization: formValue.organization,
      organizationalUnit: formValue.organizationUnit,
      country: formValue.country,
      email: this.authService.user?.email || '', // ako imaš email iz AuthService
      validFrom: formValue.validFrom,
      validTo: formValue.validTo,
      parentId: formValue.parentId || null,
      issuerId: formValue.issuerId || null,
      type: formValue.type,
      isCa: formValue.isCa || false,
    };

    console.log(' Sending payload:', payload);
    console.log('USAO');
    this.certificateService.generateCertificate(payload).subscribe({
      next: (response) => {
        alert('✅ Certificate successfully created!');
        this.certificateForm.reset();
      },
      error: (err) => {
        console.error('Error creating certificate', err);
        alert('Failed to create certificate.');
      },
    });
  }
}
