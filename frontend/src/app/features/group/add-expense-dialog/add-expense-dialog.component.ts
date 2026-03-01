import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormArray, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../../core/services/api.service';
import type { SplitType } from '../../../models/expense.model';

export interface AddExpenseDialogData {
  groupId: number;
  groupName: string;
  memberUserNames: string[];
  currentUserName: string;
  currentUserId: number;
}

interface Member {
  userId: number;
  userName: string;
}

@Component({
  selector: 'app-add-expense-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatCheckboxModule,
  ],
  templateUrl: './add-expense-dialog.component.html',
  styleUrl: './add-expense-dialog.component.scss',
})
export class AddExpenseDialogComponent implements OnInit {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<AddExpenseDialogComponent>);
  private api = inject(ApiService);
  private snackBar = inject(MatSnackBar);
  data = inject<AddExpenseDialogData>(MAT_DIALOG_DATA);

  members = new Array<Member>();
  loading = true;

  form = this.fb.nonNullable.group({
    description: ['', [Validators.required]],
    totalAmount: [0, [Validators.required, Validators.min(0.01)]],
    splitType: ['EQUAL' as SplitType, [Validators.required]],
    participants: this.fb.array([] as ReturnType<typeof this.createParticipantControl>[]),
  });

  get splitType(): SplitType {
    return this.form.get('splitType')?.value ?? 'EQUAL';
  }

  get participants(): FormArray {
    return this.form.get('participants') as FormArray;
  }

  ngOnInit(): void {
    this.api.getGroupMembers(this.data.groupName).subscribe({
      next: (res) => {
        if (Array.isArray(res)) {
          this.members = res;
          this.participants.clear();
          this.members.forEach((m) => {
            this.participants.push(this.createParticipantControl(m.userId));
          });
        }
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Failed to load group members', 'Close', { duration: 3000 });
      },
    });
  }

  private createParticipantControl(userId: number) {
    return this.fb.group({
      userId: [userId, Validators.required],
      paidAmount: [0 as number, [Validators.required, Validators.min(0)]],
      owedAmount: [0 as number, [Validators.min(0)]],
      includedInSplit: [true as boolean],
      percentage: [0 as number, [Validators.min(0), Validators.max(100)]],
    });
  }

  totalPaid(): number {
    return this.participants.controls.reduce((sum, c) => sum + (Number(c.get('paidAmount')?.value) || 0), 0);
  }

  totalPercentage(): number {
    return this.participants.controls.reduce((sum, c) => sum + (Number(c.get('percentage')?.value) || 0), 0);
  }

  submit(): void {
    const total = Number(this.form.get('totalAmount')?.value) || 0;
    const paid = this.totalPaid();
    if (Math.abs(paid - total) > 0.01) {
      this.snackBar.open(`Total paid (${paid.toFixed(2)}) must equal expense amount (${total.toFixed(2)})`, 'Close', { duration: 4000 });
      return;
    }
    const st = this.splitType;
    if (st === 'PERCENTAGE' && Math.abs(this.totalPercentage() - 100) > 0.01) {
      this.snackBar.open('Percentages must add up to 100', 'Close', { duration: 4000 });
      return;
    }

    const participantValues = this.participants.controls.map((c, i) => {
      const m = this.members[i];
      const paidAmount = Number(c.get('paidAmount')?.value) || 0;
      const owedAmount = Number(c.get('owedAmount')?.value) || 0;
      const includedInSplit = !!c.get('includedInSplit')?.value;
      const percentage = Number(c.get('percentage')?.value) || 0;
      return {
        userId: m.userId,
        paidAmount,
        owedAmount: st === 'EXACT' ? owedAmount : undefined,
        includedInSplit: st === 'EQUAL' ? includedInSplit : undefined,
        percentage: st === 'PERCENTAGE' ? percentage : undefined,
      };
    });

    const body = {
      description: this.form.get('description')?.value ?? '',
      totalAmount: total,
      groupId: this.data.groupId,
      createdByUserId: this.data.currentUserId,
      splitType: st,
      participants: participantValues,
    };

    this.api.createExpense(body).subscribe({
      next: () => {
        // Backend returns 200 with string or object; entry is in DB — always close on success
        this.dialogRef.close(true);
      },
      error: (err) => {
        const msg = this.toSnackbarMessage(err?.error);
        this.snackBar.open(msg, 'Close', { duration: 4000 });
      },
    });
  }

  private toSnackbarMessage(value: unknown): string {
    if (value == null) return 'Failed to create expense';
    if (typeof value === 'string') return value;
    if (typeof value === 'object' && value !== null && 'message' in value) {
      const m = (value as { message: unknown }).message;
      return typeof m === 'string' ? m : String(m);
    }
    return 'Failed to create expense';
  }
}
