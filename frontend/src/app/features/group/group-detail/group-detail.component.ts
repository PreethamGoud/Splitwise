import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';
import { ApiService } from '../../../core/services/api.service';
import { AddMemberDialogComponent } from '../add-member-dialog/add-member-dialog.component';
import { AddExpenseDialogComponent } from '../add-expense-dialog/add-expense-dialog.component';
import { RecordSettlementDialogComponent } from '../record-settlement-dialog/record-settlement-dialog.component';
import type { GroupDto } from '../../../models/group.model';
import type { SettleUpDto } from '../../../models/settlement.model';

@Component({
  selector: 'app-group-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatListModule,
    MatTabsModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './group-detail.component.html',
  styleUrl: './group-detail.component.scss',
})
export class GroupDetailComponent implements OnInit {
  groupName = signal('');
  group = signal<GroupDto | null>(null);
  balance = signal<Record<string, number>>({});
  suggestedSettlements = signal<SettleUpDto[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  balanceEntries = computed(() => {
    const b = this.balance();
    return Object.entries(b).filter(([, v]) => v !== 0);
  });

  constructor(
    private route: ActivatedRoute,
    public auth: AuthService,
    private api: ApiService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const name = params.get('groupName');
      if (name) {
        this.groupName.set(name);
        this.loadGroup(name);
        this.loadBalance(name);
        this.loadSuggestedSettlements(name);
      }
    });
  }

  private loadGroup(name: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.api.getGroup(name).subscribe({
      next: (res) => {
        if (res && 'groupName' in res && !('message' in res)) {
          this.group.set(res as GroupDto);
        } else if (res && 'message' in res) {
          this.error.set((res as { message: string }).message);
        }
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Failed to load group');
        this.loading.set(false);
      },
    });
  }

  private loadBalance(name: string): void {
    this.api.getGroupBalance(name).subscribe({
      next: (b) => this.balance.set(b ?? {}),
    });
  }

  private loadSuggestedSettlements(name: string): void {
    this.api.getSettleUpGroup(name).subscribe({
      next: (res) => {
        if (Array.isArray(res)) {
          this.suggestedSettlements.set(res);
        }
      },
    });
  }

  refresh(): void {
    const name = this.groupName();
    if (name) {
      this.loadGroup(name);
      this.loadBalance(name);
      this.loadSuggestedSettlements(name);
    }
  }

  openAddMember(): void {
    const name = this.groupName();
    if (!name) return;
    const ref = this.dialog.open(AddMemberDialogComponent, {
      width: '400px',
      data: { groupName: name, existingMembers: this.group()?.userNames ?? [] },
    });
    ref.afterClosed().subscribe((userName?: string) => {
      if (!userName) return;

      this.api.addUserToGroup(name, userName).subscribe({
        next: (res) => {
          if (typeof res === 'string') {
            this.snackBar.open(res, 'Close', { duration: 2000 });
          } else if (res && typeof res === 'object' && 'message' in res) {
            const msg = (res as { message: string }).message;
            this.snackBar.open(msg, 'Close', { duration: 4000 });
            return;
          }
          this.loadGroup(name);
        },
        error: (err) => {
          console.error('addUserToGroup error', err);
          this.snackBar.open(
            err?.error?.message ?? 'Failed to add member',
            'Close',
            { duration: 4000 },
          );
        },
      });
    });
  }

  openAddExpense(): void {
    const g = this.group();
    const name = this.groupName();
    if (!g || !name) return;
    const ref = this.dialog.open(AddExpenseDialogComponent, {
      width: '560px',
      data: {
        groupId: g.id,
        groupName: name,
        memberUserNames: g.userNames,
        currentUserName: this.auth.currentUser()?.userName ?? '',
        currentUserId: this.auth.currentUser()?.id,
      },
    });
    ref.afterClosed().subscribe((created: boolean) => {
      if (created) {
        this.snackBar.open('Expense added', 'Close', { duration: 2000 });
        this.loadBalance(name);
        this.loadSuggestedSettlements(name);
      }
    });
  }

  openRecordSettlement(tx: SettleUpDto): void {
    const name = this.groupName();
    if (!name) return;
    console.log('opening record-settlement dialog', name, tx);
    const ref = this.dialog.open(RecordSettlementDialogComponent, {
      width: '400px',
      data: { groupName: name, transaction: tx },
    });
    ref.afterClosed().subscribe({
      next: (recorded: boolean) => {
        console.log('record-settlement dialog closed, result=', recorded);
        if (recorded) {
          this.snackBar.open('Settlement recorded', 'Close', {
            duration: 2000,
          });
          this.loadBalance(name);
          this.loadSuggestedSettlements(name);
        }
      },
      error: (err) => {
        console.error('dialog afterClosed error', err);
        this.snackBar.open(
          'An error occurred closing settlement dialog',
          'Close',
          {
            duration: 4000,
          },
        );
      },
    });
  }

  recordSettlement(tx: SettleUpDto): void {
    const name = this.groupName();
    if (!name) return;
    console.log('recordSettlement request', name, tx);
    this.api.recordSettlement(name, tx).subscribe({
      next: (res) => {
        console.log('recordSettlement response', res);
        // support string, {message}, {success:boolean, message?}
        let msg = 'Done';
        let ok = true;
        if (typeof res === 'string') {
          msg = res;
        } else if (res && typeof res === 'object') {
          if ('message' in res && res.message) {
            msg = res.message;
          }
          if ('success' in res && res.success === false) {
            ok = false;
          }
        }
        this.snackBar.open(msg, 'Close', { duration: 2000 });
        if (ok) {
          this.loadBalance(name);
          this.loadSuggestedSettlements(name);
        }
      },
      error: (err) => {
        console.error('recordSettlement error', err);
        this.snackBar.open(
          err?.error?.message ?? 'Failed to record settlement',
          'Close',
          { duration: 4000 },
        );
      },
    });
  }
}
