<template>
  <section>
    <h1>数据看板</h1>
    <div class="grid">
      <div class="panel">
        <strong>今日订单</strong>
        <p>{{ stats?.ordersToday ?? 0 }}</p>
      </div>
      <div class="panel">
        <strong>销售额</strong>
        <p>￥{{ stats?.salesAmount ?? 0 }}</p>
      </div>
      <div class="panel">
        <strong>用户数</strong>
        <p>{{ stats?.userCount ?? 0 }}</p>
      </div>
      <div class="panel">
        <strong>鲜花数</strong>
        <p>{{ stats?.flowerCount ?? 0 }}</p>
      </div>
    </div>
    <p v-if="message">{{ message }}</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { getDashboardStats, type DashboardStats } from "@/api/admin";

const stats = ref<DashboardStats | null>(null);
const message = ref("");

onMounted(async () => {
  try {
    const { data } = await getDashboardStats();
    stats.value = data.data;
  } catch (error: any) {
    message.value = error?.response?.data?.message || "数据看板加载失败";
  }
});
</script>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

p {
  margin: 12px 0 0;
  font-size: 28px;
  font-weight: 700;
}
</style>

