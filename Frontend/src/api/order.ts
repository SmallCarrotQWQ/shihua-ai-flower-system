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

export function createOrder(cartIds?: number[], remark = "", addressId?: number) {
  return http.post<ApiResponse<Order>>("/order", { cartIds, remark, addressId });
}

export function getOrders() {
  return http.get<ApiResponse<Order[]>>("/order");
}

export function getOrderDetail(id: number) {
  return http.get<ApiResponse<Order>>(`/order/${id}`);
}

export function payOrder(id: number) {
  return http.post<ApiResponse<Order>>(`/order/${id}/pay`);
}

export function receiveOrder(id: number) {
  return http.post<ApiResponse<Order>>(`/order/${id}/receive`);
}

export function cancelOrder(id: number) {
  return http.post<ApiResponse<Order>>(`/order/${id}/cancel`);
}
