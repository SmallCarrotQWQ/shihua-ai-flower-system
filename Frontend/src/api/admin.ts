import { http } from "./http";
import type { ApiResponse, UserInfo } from "./auth";
import type { Category, Flower } from "./catalog";
import type { Order } from "./order";

export interface DashboardStats {
  ordersToday: number;
  salesAmount: number;
  userCount: number;
  flowerCount: number;
}

export interface AdminCategoryPayload {
  categoryName: string;
  parentId: number;
  sortOrder: number;
}

export interface AdminFlowerPayload {
  flowerName: string;
  categoryId: number;
  price: number;
  stock: number;
  coverImage: string;
  description: string;
  flowerLanguage: string;
  careGuide: string;
  status: number;
}

export function getDashboardStats() {
  return http.get<ApiResponse<DashboardStats>>("/admin/dashboard/stats");
}

export function getAdminFlowers() {
  return http.get<ApiResponse<Flower[]>>("/admin/flower");
}

export function createAdminFlower(payload: AdminFlowerPayload) {
  return http.post<ApiResponse<Flower>>("/admin/flower", payload);
}

export function updateAdminFlower(id: number, payload: AdminFlowerPayload) {
  return http.put<ApiResponse<Flower>>(`/admin/flower/${id}`, payload);
}

export function updateAdminFlowerStatus(id: number, status: number) {
  return http.put<ApiResponse<void>>(`/admin/flower/${id}/status`, { status });
}

export function deleteAdminFlower(id: number) {
  return http.delete<ApiResponse<void>>(`/admin/flower/${id}`);
}

export function getAdminCategories() {
  return http.get<ApiResponse<Category[]>>("/admin/category");
}

export function createAdminCategory(payload: AdminCategoryPayload) {
  return http.post<ApiResponse<Category>>("/admin/category", payload);
}

export function updateAdminCategory(id: number, payload: AdminCategoryPayload) {
  return http.put<ApiResponse<Category>>(`/admin/category/${id}`, payload);
}

export function deleteAdminCategory(id: number) {
  return http.delete<ApiResponse<void>>(`/admin/category/${id}`);
}

export function getAdminOrders() {
  return http.get<ApiResponse<Order[]>>("/admin/order");
}

export function updateAdminOrderStatus(id: number, status: number) {
  return http.put<ApiResponse<void>>(`/admin/order/${id}/status`, { status });
}

export function getAdminUsers() {
  return http.get<ApiResponse<UserInfo[]>>("/admin/user");
}

export function updateAdminUserStatus(id: number, status: number) {
  return http.put<ApiResponse<void>>(`/admin/user/${id}/status`, { status });
}

