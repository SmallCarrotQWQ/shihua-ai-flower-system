import { http } from "./http";
import type { ApiResponse } from "./auth";

export interface ReviewPayload {
  flowerId: number;
  rating: number;
  content?: string;
}

export interface Review {
  reviewId: number;
  userId: number;
  flowerId: number;
  orderId?: number;
  rating: number;
  content?: string;
  sentiment?: string;
  keywords?: string;
  createTime?: string;
}

export function createReview(orderId: number, payload: ReviewPayload) {
  return http.post<ApiResponse<Review>>(`/review/order/${orderId}`, payload);
}

export function getFlowerReviews(flowerId: number) {
  return http.get<ApiResponse<Review[]>>(`/review/flower/${flowerId}`);
}
