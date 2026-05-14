<template>
  <section class="page home">
    <div>
      <p class="eyebrow">拾花商城</p>
      <h1>拾花</h1>
      <p>鲜花分类、商品浏览、购物车和订单流程已经接入后端接口。</p>
      <div class="actions">
        <RouterLink to="/category">浏览分类</RouterLink>
        <RouterLink to="/list">查看鲜花</RouterLink>
        <RouterLink to="/cart">购物车</RouterLink>
      </div>
    </div>
    <div class="panel">
      <h2>热销鲜花</h2>
      <article v-for="flower in hotFlowers" :key="flower.flowerId" class="hot-item">
        <span>{{ flower.flowerName }}</span>
        <RouterLink :to="`/detail/${flower.flowerId}`">详情</RouterLink>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { getHotFlowers, type Flower } from "@/api/catalog";

const hotFlowers = ref<Flower[]>([]);

onMounted(async () => {
  try {
    const { data } = await getHotFlowers(5);
    hotFlowers.value = data.data;
  } catch {
    hotFlowers.value = [];
  }
});
</script>

<style scoped>
.home {
  min-height: calc(100vh - 64px);
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
  align-items: center;
}

.eyebrow {
  color: #1677ff;
  font-weight: 700;
}

h1 {
  margin: 0;
  font-size: 56px;
}

a,
button {
  height: 40px;
  border: 0;
  border-radius: 6px;
  padding: 0 14px;
  color: #ffffff;
  background: #1677ff;
  display: inline-flex;
  align-items: center;
}

.actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.hot-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #e5e7eb;
}

@media (max-width: 760px) {
  .home {
    grid-template-columns: 1fr;
    padding: 32px 0;
  }
}
</style>
