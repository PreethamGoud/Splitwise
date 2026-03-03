import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { ApiService } from '../../../core/services/api.service';
import { finalize } from 'rxjs/operators';
import type { SettleUpDto } from '../../../models/settlement.model';

export interface RecordSettlementDialogData {
  groupName: string;
  transaction: SettleUpDto;
}

@Component({
  selector: 'app-record-settlement-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Record settlement</h2>
    <mat-dialog-content>
      <p>
        Record that <strong>{{ data.transaction.paidFrom }}</strong> paid
        <strong>{{ data.transaction.amount | number: '1.2-2' }}</strong> to
        <strong>{{ data.transaction.paidTo }}</strong> in {{ data.groupName }}?
      </p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close [disabled]="loading">Cancel</button>
      <button
        mat-flat-button
        color="primary"
        (click)="confirm()"
        [disabled]="loading"
      >
        <span *ngIf="!loading">Record</span>
        <mat-progress-spinner
          *ngIf="loading"
          diameter="20"
          mode="indeterminate"
        ></mat-progress-spinner>
      </button>
    </mat-dialog-actions>
  `,
})
export class RecordSettlementDialogComponent {
  loading = false;

  constructor(
    private dialogRef: MatDialogRef<RecordSettlementDialogComponent>,
    private api: ApiService,
    @Inject(MAT_DIALOG_DATA) public data: RecordSettlementDialogData,
  ) {}

  confirm(): void {
    this.loading = true;
    console.log('RecordSettlementDialog.confirm()', this.data);
    this.api
      .recordSettlement(this.data.groupName, this.data.transaction)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (res) => {
          console.log('dialog API recordSettlement succeeded', res);
          this.dialogRef.close(true);
        },
        error: (err) => {
          console.error('dialog API recordSettlement failed', err);
          this.dialogRef.close(false);
        },
      });
  }
}
