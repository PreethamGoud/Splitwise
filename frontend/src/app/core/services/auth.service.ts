import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { UserDto, LoginRequest, SignupRequest } from '../../models/user.model';

const TOKEN_KEY = 'splitwise_token';
const USER_KEY = 'splitwise_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = `${environment.apiUrl}/public`;
  private tokenSignal = signal<string | null>(this.getStoredToken());
  private userSignal = signal<UserDto | null>(this.getStoredUser());

  token = this.tokenSignal.asReadonly();
  currentUser = this.userSignal.asReadonly();
  isLoggedIn = computed(() => !!this.tokenSignal());

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  login(body: LoginRequest): Observable<string> {
    return this.http.post<string>(`${this.api}/login`, body, {
      responseType: 'text' as 'json',
    }).pipe(
      tap((jwt) => {
        this.setToken(jwt);
      })
    );
  }

  signup(body: SignupRequest): Observable<UserDto | { message: string }> {
    return this.http.post<UserDto | { message: string }>(`${this.api}/signup`, body).pipe(
      tap((res) => {
        if ('userName' in res) {
          this.setUser(res as UserDto);
        }
      })
    );
  }

  logout(): void {
    this.tokenSignal.set(null);
    this.userSignal.set(null);
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this.tokenSignal();
  }

  fetchCurrentUser(): Observable<UserDto | { message: string }> {
    return this.http.get<UserDto | { message: string }>(`${environment.apiUrl}/user`).pipe(
      tap((res) => {
        if (res && 'userName' in res) {
          this.setUser(res as UserDto);
        }
      }),
      catchError(() => {
        this.logout();
        return of({ message: 'Session expired' });
      })
    );
  }

  setUser(user: UserDto): void {
    this.userSignal.set(user);
    try {
      localStorage.setItem(USER_KEY, JSON.stringify(user));
    } catch {}
  }

  private setToken(jwt: string): void {
    this.tokenSignal.set(jwt);
    localStorage.setItem(TOKEN_KEY, jwt);
  }

  private getStoredToken(): string | null {
    try {
      return localStorage.getItem(TOKEN_KEY);
    } catch {
      return null;
    }
  }

  private getStoredUser(): UserDto | null {
    try {
      const raw = localStorage.getItem(USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }
}
