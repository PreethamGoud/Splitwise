export interface SettleUpDto {
  paidFrom: string;
  paidTo: string;
  amount: number;
}

export interface ConsolidatedSettlementMiniDto {
  paidFrom: string;
  paidTo: string;
  amount: number;
  groupName: string;
}

export interface ConsolidatedSettlementDto {
  paidFrom: string;
  paidTo: string;
  totalAmount: number;
  individualGroupShare?: ConsolidatedSettlementMiniDto[];
}

export interface AllGroupsSettledDto {
  grandTotalAmount: number;
  shareWithFriends: ConsolidatedSettlementDto[];
}
