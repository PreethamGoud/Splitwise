import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

export interface AddMemberDialogData {
  groupName: string;
  existingMembers: string[];
}

@Component({
  selector: 'app-add-member-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  template: `
    <h2 mat-dialog-title>Add member</h2>
    <mat-dialog-content>
      <p>Add a user to <strong>{{ data.groupName }}</strong> by their username.</p>
      <form [formGroup]="form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Username</mat-label>
          <input matInput formControlName="userName" placeholder="username" />
          @if (form.get('userName')?.hasError('required') && form.get('userName')?.touched) {
            <mat-error>Username is required</mat-error>
          }
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-flat-button color="primary" (click)="submit()" [disabled]="form.invalid">
        Add
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `
      .full-width {
        width: 100%;
        display: block;
      }
      mat-dialog-content {
        min-width: 320px;
      }
      p {
        margin-bottom: 1rem;
      }
    `,
  ],
})
export class AddMemberDialogComponent {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<AddMemberDialogComponent>);
  data = inject<AddMemberDialogData>(MAT_DIALOG_DATA);
  form = this.fb.nonNullable.group({
    userName: ['', [Validators.required]],
  });

  submit(): void {
    if (this.form.invalid) return;
    this.dialogRef.close(this.form.getRawValue().userName);
  }
}
