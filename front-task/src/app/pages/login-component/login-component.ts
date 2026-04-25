import { Component, inject } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/authService';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-component',
  imports: [ReactiveFormsModule],
  templateUrl: './login-component.html',
  styleUrl: './login-component.css'
})
export class LoginComponent {
  loginForm: FormGroup;
  auth = inject(AuthService);
  private router = inject(Router);

  constructor(private fb: FormBuilder) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }
  onSubmit(): void {
    if (this.loginForm.valid) {
      const { username, password } = this.loginForm.value;
      console.log(this.loginForm.value);
      this.auth.login(username!, password!).subscribe({
        next: (response) => {
          console.log("SUCCESSFUL LOGIN");
          sessionStorage.setItem('roles', response.roles[0]);
        console.log(sessionStorage.getItem('roles'))
          this.auth.setUserId(response['userId']);
          console.log(this.auth.getUserId$());
          this.router.navigateByUrl('/tasks');
        },
        error: (err) => console.error('Error: ', err),
      });
    }
  }

}
