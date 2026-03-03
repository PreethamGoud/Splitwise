export type GroupTransactionType = 'EXPENSE' | 'SETTLEMENT';

export interface GroupTransactionDto {
  date: string;
  type: GroupTransactionType;
  description: string;
  paidFrom?: string | null;
  paidTo?: string | null;
  amount: number;
}

