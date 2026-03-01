export type SplitType = 'EQUAL' | 'EXACT' | 'PERCENTAGE';

export interface ParticipantDto {
  userId: number;
  paidAmount?: number;
  owedAmount?: number;
  includedInSplit?: boolean;
  percentage?: number;
}

export interface CreateExpenseDto {
  description: string;
  totalAmount: number;
  groupId: number;
  createdByUserId: number;
  splitType: SplitType;
  participants: ParticipantDto[];
}
