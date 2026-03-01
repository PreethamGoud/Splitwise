export interface GroupDto {
  id: number;
  groupName: string;
  description?: string;
  userNames: string[];
}

export interface CreateGroupRequest {
  groupName: string;
  description?: string;
}
