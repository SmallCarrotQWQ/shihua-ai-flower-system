import { http } from "./http";
import type { ApiResponse } from "./auth";

export interface OrderItem {
  itemId: number;
  flowerId: number;
  flowerName: string;
  price: number;
  quantity: number;
  subtotal: number;
}

export interface Order {
  orderId: number;
  totalAmount: number;
  status: number;
  addressId?: number;
  remark?: string;
  createTime?: string;
  items: OrderItem[];
}

export function createOrder(cartIds?: number[], remark = "") {
  return http.post<ApiResponse<Order>>("/order", { cartIds, remark });
}

export function getOrders() {
  return http.get<ApiResponse<Order[]>>("/order");
}

