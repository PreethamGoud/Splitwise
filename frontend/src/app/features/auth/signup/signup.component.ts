import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
  ],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss',
})
export class SignupComponent {
  private fb = inject(FormBuilder);
  loading = false;
  form = this.fb.nonNullable.group({
    userName: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    phoneNumber: [''],
  });

  constructor(
    private auth: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  onSubmit(): void {
    if (this.form.invalid || this.loading) return;
    this.loading = true;
    const raw = this.form.getRawValue();
    const body = {
      userName: raw.userName,
      email: raw.email,
      password: raw.password,
      ...(raw.phoneNumber ? { phoneNumber: raw.phoneNumber } : {}),
    };
    this.auth.signup(body).subscribe({
      next: (res) => {
        this.loading = false;
        if (res && 'message' in res) {
          this.snackBar.open((res as { message: string }).message, 'Close', { duration: 4000 });
          return;
        }
        this.snackBar.open('Account created. Please sign in.', 'Close', { duration: 3000 });
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.loading = false;
        const msg = err?.error?.message ?? err?.error ?? 'Signup failed';
        this.snackBar.open(String(msg), 'Close', { duration: 4000 });
      },
    });
  }
}
