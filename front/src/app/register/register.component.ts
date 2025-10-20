import { Component } from '@angular/core';
import { RegistrationService } from '../../services/registration.service';
import { UserRegistrationRequestDto } from '../../dto/registration/register.dto';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  registerForm: FormGroup;
  organizations: string[] = [
    'Google',
    'Microsoft',
    'Tesla',
    'Amazon',
    'OpenAI',
    'SpaceX',
  ];
  selectedOrganization: string = '';

  constructor(
    private fb: FormBuilder,
    private registrationService: RegistrationService
  ) {
    this.registerForm = this.fb.group(
      {
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: [
          '',
          [
            Validators.required,
            Validators.pattern(
              /^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/
            ),
          ],
        ],
        repeatPassword: ['', Validators.required],
        organization: ['', Validators.required],
      },
      { validators: this.passwordsMatchValidator }
    );
  }

  // Custom validator za poklapanje lozinki
  private passwordsMatchValidator(group: FormGroup) {
    const password = group.get('password')?.value;
    const repeatPassword = group.get('repeatPassword')?.value;
    return password === repeatPassword ? null : { passwordsMismatch: true };
  }

  onRegister() {
    if (this.registerForm.invalid) {
      if (this.registerForm.errors?.['passwordsMismatch']) {
        alert('Passwords do not match.');
      } else {
        alert('Please fill in all required fields correctly.');
      }
      return;
    }

    const formValues = this.registerForm.value;
    const request: UserRegistrationRequestDto = {
      firstName: formValues.firstName,
      lastName: formValues.lastName,
      email: formValues.email,
      password: formValues.password,
      organization: formValues.organization,
    };

    this.registrationService.registerUser(request).subscribe({
      next: (request) => {
        alert(request);
        this.registerForm.reset();
      },
      error: (err) => {
        console.error(err);
      },
    });
  }
}
