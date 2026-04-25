import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError, BehaviorSubject, map, of } from 'rxjs';
import { environment } from '../environments/environment';
import { User } from '../entity/User'

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private url: string = environment.apiUrl;
  private userId$: number = 0;

  constructor(private http: HttpClient, private router: Router) { }

  login(username: string, password: string): Observable<any> {
    const body = new HttpParams()
      .set('username', username)
      .set('password', password);

    return this.http.post(`${this.url}/login`, body.toString(), {
      headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded'),
      withCredentials: true 
    }).pipe(
      tap(() => {
         this.isAuthenticatedSubject.next(true)
        this.router.navigate(['/tasks']);
      }),
      catchError((error) => {
        if (error.status === 401) {
          alert('Неверный логин или пароль!');
        }
        return throwError(() => error);
      })
    );
  }

  logout(): Observable<any> {
    this.isAuthenticatedSubject.next(false);
    this.setUserId(-1);
    return this.http.post(`${this.url}/logout`, {}, { withCredentials: true });
  }

  setUserId(id: number): void {
    this.userId$ = id;
  }

  getUserId$(): number {
    return this.userId$;
  }

  getUser(): Observable<User>{
    return this.http.get<User>(`${this.url}/auth/me`);
  }

  checkSession(): Observable<boolean> {
    return this.http.get('/auth/status', { withCredentials: true }).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }
}