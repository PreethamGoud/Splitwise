import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../core/services/auth.service';
import { ApiService } from '../../core/services/api.service';
import { CreateGroupDialogComponent } from './create-group-dialog/create-group-dialog.component';
import type { UserDto } from '../../models/user.model';
import type { GroupDto } from '../../models/group.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatListModule,
    MatChipsModule,
    MatDialogModule,
    MatMenuModule,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  user = signal<UserDto | null>(null);
  balanceAll = signal<Record<string, number> | null>(null);
  settledAll = signal<{ paidFrom: string; paidTo: string; amount: number }[] | null>(null);
  loading = signal(true);
  balanceError = signal<string | null>(null);

  groups = computed(() => this.user()?.groups ?? []);
  balanceEntries = computed(() => {
    const b = this.balanceAll();
    if (!b) return [];
    return Object.entries(b).filter(([, v]) => v !== 0);
  });

  constructor(
    public auth: AuthService,
    private api: ApiService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.auth.fetchCurrentUser().subscribe((res) => {
      if (res && 'userName' in res) {
        this.user.set(res as UserDto);
      }
    });
    this.api.getBalanceAll().subscribe({
      next: (res) => {
        if (res && typeof res === 'object' && !('message' in res)) {
          this.balanceAll.set(res as Record<string, number>);
        } else if (res && typeof res === 'object' && 'message' in res) {
          this.balanceError.set((res as { message: string }).message);
        }
        this.loading.set(false);
      },
      error: (err) => {
        this.balanceError.set(err?.error?.message ?? 'Failed to load balances');
        this.loading.set(false);
      },
    });
    this.api.getSettledBalanceAll().subscribe({
      next: (res) => {
        if (Array.isArray(res)) {
          this.settledAll.set(res);
        }
      },
    });
  }

  openCreateGroup(): void {
    const ref = this.dialog.open(CreateGroupDialogComponent, {
      width: '400px',
      disableClose: false,
    });
    ref.afterClosed().subscribe((result?: { groupName: string; description?: string }) => {
      if (!result?.groupName) return;
      const currentUser = this.auth.currentUser();
      this.api.createGroup(result).subscribe({
        next: (res) => {
          if (res && 'message' in res) {
            this.snackBar.open((res as { message: string }).message, 'Close', { duration: 4000 });
            return;
          }
          // Backend does not add creator to group; add current user to the new group
          const userName = currentUser?.userName;
          if (userName) {
            this.api.addUserToGroup(result.groupName, userName).subscribe({
              next: () => {
                this.snackBar.open('Group created', 'Close', { duration: 2000 });
                this.auth.fetchCurrentUser().subscribe((u) => {
                  if (u && 'groups' in u) this.user.set(u as UserDto);
                });
              },
              error: () => {
                this.snackBar.open('Group created; add yourself via Add member.', 'Close', { duration: 4000 });
                this.auth.fetchCurrentUser().subscribe((u) => {
                  if (u && 'groups' in u) this.user.set(u as UserDto);
                });
              },
            });
          } else {
            this.snackBar.open('Group created', 'Close', { duration: 2000 });
            this.auth.fetchCurrentUser().subscribe((u) => {
              if (u && 'groups' in u) this.user.set(u as UserDto);
            });
          }
        },
        error: (err) => {
          this.snackBar.open(err?.error?.message ?? 'Failed to create group', 'Close', { duration: 4000 });
        },
      });
    });
  }

  logout(): void {
    this.auth.logout();
  }
}
