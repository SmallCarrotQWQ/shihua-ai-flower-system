<template>
  <section class="page">
    <h2>我的订单</h2>
    <article v-for="order in orders" :key="order.orderId" class="order">
      <header>
        <strong>订单 #{{ order.orderId }}</strong>
        <span>{{ statusText(order.status) }}</span>
      </header>
      <p>金额：￥{{ order.totalAmount }}</p>
      <p v-for="item in order.items" :key="item.itemId">
        {{ item.flowerName }} x {{ item.quantity }}
        <button v-if="order.status === 3" type="button" @click="prepareReview(order.orderId, item.flowerId)">评价</button>
      </p>
      <button v-if="order.status === 0" type="button" @click="pay(order.orderId)">模拟支付</button>
      <button v-if="order.status === 0 || order.status === 1" type="button" @click="cancel(order.orderId)">取消订单</button>
      <button v-if="order.status === 2" type="button" @click="receive(order.orderId)">确认收货</button>
    </article>

    <div v-if="review.orderId" class="review">
      <h3>提交评价</h3>
      <select v-model.number="review.rating">
        <option :value="5">5分</option>
        <option :value="4">4分</option>
        <option :value="3">3分</option>
        <option :value="2">2分</option>
        <option :value="1">1分</option>
      </select>
      <textarea v-model="review.content" placeholder="写下评价"></textarea>
      <button type="button" @click="submitReview">提交评价</button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import type { Order } from "@/api/order";
import { cancelOrder, getOrders, payOrder, receiveOrder } from "@/api/order";
import { createReview } from "@/api/review";

const orders = ref<Order[]>([]);
const review = reactive({ orderId: 0, flowerId: 0, rating: 5, content: "" });

onMounted(load);

async function load() {
  const response = await getOrders();
  orders.value = response.data.data;
}

async function pay(id: number) {
  await payOrder(id);
  await load();
}

async function cancel(id: number) {
  await cancelOrder(id);
  await load();
}

async function receive(id: number) {
  await receiveOrder(id);
  await load();
}

function prepareReview(orderId: number, flowerId: number) {
  review.orderId = orderId;
  review.flowerId = flowerId;
  review.rating = 5;
  review.content = "";
}

async function submitReview() {
  await createReview(review.orderId, { flowerId: review.flowerId, rating: review.rating, content: review.content });
  review.orderId = 0;
  await load();
}

function statusText(status: number) {
  return ["待支付", "待发货", "待收货", "已完成", "已取消"][status] ?? "未知";
}
</script>

<style scoped>
.page {
  padding: 24px;
}
.order,
.review {
  border: 1px solid #e5e7eb;
  padding: 12px;
  margin-bottom: 12px;
}
header {
  display: flex;
  justify-content: space-between;
}
button {
  margin-right: 8px;
}
textarea {
  display: block;
  width: 100%;
  min-height: 80px;
  margin: 10px 0;
}
</style>
