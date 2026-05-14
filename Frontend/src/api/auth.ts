import { http } from "./http";

export interface UserInfo {
  userId: number;
  username: string;
  gender: number;
  phone?: string;
  email?: string;
  avatar?: string;
  role: string;
  status: number;
}

export interface LoginResult {
  token: string;
  userInfo: UserInfo;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

export interface LoginPayload {
  username: string;
  password: string;
}

export interface RegisterPayload extends LoginPayload {
  confirmPassword: string;
  gender: number;
  phone: string;
}

export function login(payload: LoginPayload) {
  return http.post<ApiResponse<LoginResult>>("/user/login", payload);
}

export function register(payload: RegisterPayload) {
  return http.post<ApiResponse<LoginResult>>("/user/register", payload);
}

export function getCurrentUser() {
  return http.get<ApiResponse<UserInfo>>("/user/info");
}

