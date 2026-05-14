import { http } from "./http";
import type { ApiResponse } from "./auth";

export interface CartItem {
  cartId: number;
  flowerId: number;
  flowerName: string;
  price: number;
  stock: number;
  coverImage?: string;
  quantity: number;
  subtotal: number;
}

export function getCart() {
  return http.get<ApiResponse<CartItem[]>>("/cart");
}

export function addCart(flowerId: number, quantity = 1) {
  return http.post<ApiResponse<CartItem[]>>("/cart", { flowerId, quantity });
}

export function updateCart(cartId: number, quantity: number) {
  return http.put<ApiResponse<CartItem[]>>(`/cart/${cartId}`, { quantity });
}

export function deleteCart(cartId: number) {
  return http.delete<ApiResponse<CartItem[]>>(`/cart/${cartId}`);
}

