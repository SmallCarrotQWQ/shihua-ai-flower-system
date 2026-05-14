<template>
  <section class="page">
    <div class="toolbar">
      <h1>鲜花列表</h1>
      <input v-model.trim="keyword" placeholder="搜索花名或花语" @keyup.enter="loadFlowers" />
      <button type="button" @click="loadFlowers">搜索</button>
    </div>
    <div class="grid">
      <article v-for="flower in flowers" :key="flower.flowerId" class="panel item">
        <strong>{{ flower.flowerName }}</strong>
        <span>{{ flower.categoryName }}</span>
        <span>￥{{ flower.price }}</span>
        <span>库存：{{ flower.stock }}</span>
        <RouterLink :to="`/detail/${flower.flowerId}`">查看详情</RouterLink>
      </article>
    </div>
    <p v-if="error" class="error">{{ error }}</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { getFlowers, type Flower } from "@/api/catalog";

const route = useRoute();
const flowers = ref<Flower[]>([]);
const keyword = ref("");
const error = ref("");

async function loadFlowers() {
  error.value = "";
  try {
    const categoryId = route.query.categoryId ? Number(route.query.categoryId) : undefined;
    const { data } = await getFlowers({ categoryId, keyword: keyword.value || undefined });
    flowers.value = data.data;
  } catch {
    error.value = "鲜花列表加载失败";
  }
}

onMounted(loadFlowers);
watch(() => route.query.categoryId, loadFlowers);
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 28px 0 16px;
}

.toolbar h1 {
  margin-right: auto;
}

input {
  height: 38px;
  border: 1px solid #d0d5dd;
  border-radius: 6px;
  padding: 0 10px;
}

button,
a {
  border: 0;
  border-radius: 6px;
  padding: 10px 14px;
  color: #ffffff;
  background: #1677ff;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 14px;
}

.item {
  display: grid;
  gap: 8px;
}

.error {
  color: #d92d20;
}
</style>

