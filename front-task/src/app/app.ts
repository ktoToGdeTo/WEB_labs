import { Component, inject, OnChanges, OnInit } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterOutlet, RouterLinkWithHref } from '@angular/router';
import { AuthService } from './core/services/authService';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ReactiveFormsModule, AsyncPipe, RouterLinkWithHref],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected auth = inject(AuthService);
  router = inject(Router);
  
  logout(): void {
    this.auth.logout().subscribe();
    this.router.navigate(['/login']);
  }
}
