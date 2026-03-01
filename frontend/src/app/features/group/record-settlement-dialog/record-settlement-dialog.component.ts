import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { ApiService } from '../../../core/services/api.service';
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
        <strong>{{ data.transaction.amount | number:'1.2-2' }}</strong> to
        <strong>{{ data.transaction.paidTo }}</strong> in {{ data.groupName }}?
      </p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-flat-button color="primary" (click)="confirm()">Record</button>
    </mat-dialog-actions>
  `,
})
export class RecordSettlementDialogComponent {
  constructor(
    private dialogRef: MatDialogRef<RecordSettlementDialogComponent>,
    private api: ApiService,
    @Inject(MAT_DIALOG_DATA) public data: RecordSettlementDialogData
  ) {}

  confirm(): void {
    this.api.recordSettlement(this.data.groupName, this.data.transaction).subscribe({
      next: () => this.dialogRef.close(true),
      error: () => this.dialogRef.close(false),
    });
  }
}
