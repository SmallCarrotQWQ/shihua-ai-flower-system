<template>
  <section class="page">
    <div v-if="flower" class="panel detail">
      <h1>{{ flower.flowerName }}</h1>
      <p>分类：{{ flower.categoryName }}</p>
      <p>价格：￥{{ flower.price }}</p>
      <p>库存：{{ flower.stock }}</p>
      <p>花语：{{ flower.flowerLanguage || "暂无" }}</p>
      <p>描述：{{ flower.description || "暂无" }}</p>
      <p>养护指南：{{ flower.careGuide || "暂无" }}</p>
      <label>
        购买数量
        <input v-model.number="quantity" min="1" type="number" />
      </label>
      <button type="button" @click="add">加入购物车</button>
      <p v-if="message">{{ message }}</p>
    </div>
    <p v-if="error" class="error">{{ error }}</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getFlowerDetail, type Flower } from "@/api/catalog";
import { addCart } from "@/api/cart";
import { useAuthStore } from "@/stores/auth";

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();
const flower = ref<Flower | null>(null);
const quantity = ref(1);
const message = ref("");
const error = ref("");

onMounted(async () => {
  try {
    const { data } = await getFlowerDetail(String(route.params.id));
    flower.value = data.data;
  } catch {
    error.value = "商品详情加载失败";
  }
});

async function add() {
  if (!auth.token) {
    router.push("/login");
    return;
  }
  if (!flower.value) return;
  try {
    await addCart(flower.value.flowerId, quantity.value);
    message.value = "已加入购物车";
  } catch (exception: any) {
    message.value = exception?.response?.data?.message || "加入购物车失败";
  }
}
</script>

<style scoped>
.detail {
  margin-top: 28px;
  display: grid;
  gap: 10px;
}

input {
  width: 100px;
  height: 36px;
  margin-left: 8px;
}

button {
  width: 140px;
  height: 40px;
  border: 0;
  border-radius: 6px;
  color: #ffffff;
  background: #1677ff;
}

.error {
  color: #d92d20;
}
</style>

