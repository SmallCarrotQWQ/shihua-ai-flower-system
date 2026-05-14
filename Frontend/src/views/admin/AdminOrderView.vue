<template>
  <section>
    <h1>订单管理</h1>
    <table>
      <thead>
        <tr><th>订单号</th><th>金额</th><th>状态</th><th>商品</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="order in orders" :key="order.orderId">
          <td>{{ order.orderId }}</td>
          <td>￥{{ order.totalAmount }}</td>
          <td>{{ statusText(order.status) }}</td>
          <td>{{ order.items.map(item => `${item.flowerName}x${item.quantity}`).join("，") }}</td>
          <td>
            <button @click="setStatus(order.orderId, 2)">发货</button>
            <button @click="setStatus(order.orderId, 4)">取消</button>
          </td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { getAdminOrders, updateAdminOrderStatus } from "@/api/admin";
import type { Order } from "@/api/order";

const orders = ref<Order[]>([]);

onMounted(load);

async function load() {
  const { data } = await getAdminOrders();
  orders.value = data.data;
}

async function setStatus(id: number, status: number) {
  await updateAdminOrderStatus(id, status);
  await load();
}

function statusText(status: number) {
  return ["待付款", "已付款", "已发货", "已完成", "已取消"][status] || "未知";
}
</script>

<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
}

td,
th {
  border: 1px solid #e5e7eb;
  padding: 8px;
}
</style>

