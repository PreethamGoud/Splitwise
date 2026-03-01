export interface UserDto {
  id: number;
  userName: string;
  email: string;
  password?: string;
  phoneNumber?: string;
  groups: string[];
  roles: string[];
}

export interface UserDtoOrError {
  message?: string;
}

export interface LoginRequest {
  userName: string;
  password: string;
}

export interface SignupRequest {
  userName: string;
  email: string;
  password: string;
  phoneNumber?: string;
}
