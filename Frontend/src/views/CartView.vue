<template>
  <section class="page">
    <h1>购物车</h1>
    <div v-if="items.length" class="panel list">
      <article v-for="item in items" :key="item.cartId" class="row">
        <div>
          <strong>{{ item.flowerName }}</strong>
          <p>单价：￥{{ item.price }}，库存：{{ item.stock }}</p>
        </div>
        <input v-model.number="item.quantity" min="1" type="number" @change="changeQuantity(item.cartId, item.quantity)" />
        <span>小计：￥{{ item.subtotal }}</span>
        <button type="button" @click="remove(item.cartId)">删除</button>
      </article>
      <footer>
        <strong>合计：￥{{ total }}</strong>
        <select v-model.number="addressId">
          <option :value="0">不选择地址</option>
          <option v-for="address in addresses" :key="address.addressId" :value="address.addressId">
            {{ address.receiver }} {{ address.phone }}
          </option>
        </select>
        <button type="button" @click="submitOrder">提交订单</button>
      </footer>
    </div>
    <div v-else class="panel muted">购物车为空，请先选择鲜花。</div>
    <p v-if="message">{{ message }}</p>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { deleteCart, getCart, updateCart, type CartItem } from "@/api/cart";
import { createOrder } from "@/api/order";
import { getAddresses, type Address } from "@/api/user";
import { useAuthStore } from "@/stores/auth";

const router = useRouter();
const auth = useAuthStore();
const items = ref<CartItem[]>([]);
const addresses = ref<Address[]>([]);
const addressId = ref(0);
const message = ref("");
const total = computed(() => items.value.reduce((sum, item) => sum + Number(item.subtotal), 0).toFixed(2));

onMounted(loadCart);

async function loadCart() {
  if (!auth.token) {
    router.push("/login");
    return;
  }
  try {
    const { data } = await getCart();
    items.value = data.data;
    const addressResponse = await getAddresses();
    addresses.value = addressResponse.data.data;
    const defaultAddress = addresses.value.find((item) => item.isDefault === 1);
    addressId.value = defaultAddress?.addressId ?? 0;
  } catch (exception: any) {
    message.value = exception?.response?.data?.message || "购物车加载失败";
  }
}

async function changeQuantity(cartId: number, quantity: number) {
  const { data } = await updateCart(cartId, quantity);
  items.value = data.data;
}

async function remove(cartId: number) {
  const { data } = await deleteCart(cartId);
  items.value = data.data;
}

async function submitOrder() {
  try {
    const { data } = await createOrder(items.value.map((item) => item.cartId), "前端提交订单", addressId.value || undefined);
    items.value = [];
    message.value = `订单提交成功，订单号：${data.data.orderId}`;
  } catch (exception: any) {
    message.value = exception?.response?.data?.message || "提交订单失败";
  }
}
</script>

<style scoped>
h1 {
  margin-top: 28px;
}

.list {
  display: grid;
  gap: 12px;
}

.row,
footer {
  display: grid;
  grid-template-columns: 1fr 100px 140px 80px;
  gap: 12px;
  align-items: center;
}

footer {
  grid-template-columns: 1fr 180px 120px;
}

input {
  height: 36px;
}

button {
  height: 36px;
  border: 0;
  border-radius: 6px;
  color: #ffffff;
  background: #1677ff;
}
</style>
