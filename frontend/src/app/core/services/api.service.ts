import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { UserDto, UserDtoOrError } from '../../models/user.model';
import type { GroupDto, CreateGroupRequest } from '../../models/group.model';
import type { CreateExpenseDto } from '../../models/expense.model';
import type { SettleUpDto, AllGroupsSettledDto } from '../../models/settlement.model';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // —— Public ———————————————————————————————————————————————————————————————
  healthCheck(): Observable<string> {
    return this.http.get(`${this.base}/public/healthCheck`, { responseType: 'text' });
  }

  // —— User ——————————————————————————————————————————————————————————————————
  getCurrentUser(): Observable<UserDto | UserDtoOrError> {
    return this.http.get<UserDto | UserDtoOrError>(`${this.base}/user`);
  }

  getBalanceAll(): Observable<Record<string, number> | UserDtoOrError> {
    return this.http.get<Record<string, number> | UserDtoOrError>(`${this.base}/user/getBalance/all`);
  }

  getSettledBalanceAll(): Observable<SettleUpDto[] | UserDtoOrError> {
    return this.http.get<SettleUpDto[] | UserDtoOrError>(`${this.base}/user/getBalance/all/settled`);
  }

  addRoleToUser(userName: string, roleName: string): Observable<boolean | UserDtoOrError> {
    return this.http.post<boolean | UserDtoOrError>(
      `${this.base}/user/${encodeURIComponent(userName)}/role/${encodeURIComponent(roleName)}`,
      {}
    );
  }

  // —— Group —————————————————————————————————————————————————————————————————
  getGroupMembers(groupName: string): Observable<{ userId: number; userName: string }[] | UserDtoOrError> {
    return this.http.get<{ userId: number; userName: string }[] | UserDtoOrError>(
      `${this.base}/group/${encodeURIComponent(groupName)}/members`
    );
  }

  getGroup(groupName: string): Observable<GroupDto | UserDtoOrError> {
    return this.http.get<GroupDto | UserDtoOrError>(
      `${this.base}/group/${encodeURIComponent(groupName)}`
    );
  }

  createGroup(body: CreateGroupRequest): Observable<GroupDto | UserDtoOrError> {
    return this.http.post<GroupDto | UserDtoOrError>(`${this.base}/group`, body);
  }

  addUserToGroup(groupName: string, userName: string): Observable<string | UserDtoOrError> {
    return this.http.post<string | UserDtoOrError>(
      `${this.base}/group/${encodeURIComponent(groupName)}/user/${encodeURIComponent(userName)}`,
      {}
    );
  }

  getGroupBalance(groupName: string): Observable<Record<string, number>> {
    return this.http.get<Record<string, number>>(
      `${this.base}/group/getBalance/${encodeURIComponent(groupName)}`
    );
  }

  // —— Expense ——————————————————————————————————————————————————————————————
  /** Backend returns plain text "Expense created successfully", not JSON — use responseType: 'text' to avoid parse error. */
  createExpense(body: CreateExpenseDto): Observable<string> {
    return this.http.post(`${this.base}/expense`, body, { responseType: 'text' });
  }

  // —— Settlement —————————————————————————————————————————————————────────———
  getSettleUpGroup(groupName: string): Observable<SettleUpDto[] | UserDtoOrError> {
    return this.http.get<SettleUpDto[] | UserDtoOrError>(
      `${this.base}/settle/group/${encodeURIComponent(groupName)}`
    );
  }

  getAllSettledUser(): Observable<AllGroupsSettledDto | UserDtoOrError> {
    return this.http.get<AllGroupsSettledDto | UserDtoOrError>(
      `${this.base}/settle/getAllSettled/user`
    );
  }

  getAllSettledUserGroup(groupName: string): Observable<AllGroupsSettledDto | UserDtoOrError> {
    return this.http.get<AllGroupsSettledDto | UserDtoOrError>(
      `${this.base}/settle/getAllSettled/user/group/${encodeURIComponent(groupName)}`
    );
  }

  recordSettlement(groupName: string, body: SettleUpDto): Observable<string | UserDtoOrError> {
    return this.http.post<string | UserDtoOrError>(
      `${this.base}/settle/group/${encodeURIComponent(groupName)}`,
      body
    );
  }
}
