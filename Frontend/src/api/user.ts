import { http } from "./http";
import type { ApiResponse, UserInfo } from "./auth";

export interface Address {
  addressId: number;
  receiver: string;
  phone: string;
  province?: string;
  city?: string;
  district?: string;
  detail: string;
  isDefault: number;
}

export interface AddressPayload {
  receiver: string;
  phone: string;
  province?: string;
  city?: string;
  district?: string;
  detail: string;
  isDefault?: number;
}

export function updateProfile(payload: Partial<UserInfo>) {
  return http.put<ApiResponse<UserInfo>>("/user/info", payload);
}

export function changePassword(oldPassword: string, newPassword: string) {
  return http.put<ApiResponse<null>>("/user/password", { oldPassword, newPassword });
}

export function getAddresses() {
  return http.get<ApiResponse<Address[]>>("/user/address");
}

export function createAddress(payload: AddressPayload) {
  return http.post<ApiResponse<Address>>("/user/address", payload);
}

export function updateAddress(id: number, payload: AddressPayload) {
  return http.put<ApiResponse<Address>>(`/user/address/${id}`, payload);
}

export function deleteAddress(id: number) {
  return http.delete<ApiResponse<null>>(`/user/address/${id}`);
}

export function setDefaultAddress(id: number) {
  return http.put<ApiResponse<null>>(`/user/address/${id}/default`);
}
