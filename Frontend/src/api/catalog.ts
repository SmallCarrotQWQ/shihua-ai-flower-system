import { http } from "./http";
import type { ApiResponse } from "./auth";

export interface Category {
  categoryId: number;
  categoryName: string;
  parentId: number;
  sortOrder: number;
}

export interface Flower {
  flowerId: number;
  flowerName: string;
  categoryId: number;
  categoryName: string;
  price: number;
  stock: number;
  coverImage?: string;
  description?: string;
  flowerLanguage?: string;
  careGuide?: string;
  salesCount: number;
  status: number;
}

export function getCategories() {
  return http.get<ApiResponse<Category[]>>("/category");
}

export function getFlowers(params?: { categoryId?: number; keyword?: string }) {
  return http.get<ApiResponse<Flower[]>>("/flower", { params });
}

export function getHotFlowers(limit = 8) {
  return http.get<ApiResponse<Flower[]>>("/flower/hot", { params: { limit } });
}

export function getFlowerDetail(id: number | string) {
  return http.get<ApiResponse<Flower>>(`/flower/${id}`);
}
