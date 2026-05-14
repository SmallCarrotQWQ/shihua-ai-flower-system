<template>
  <div>
    <header class="topbar">
      <RouterLink class="brand" to="/home">拾花</RouterLink>
      <nav>
        <RouterLink to="/home">首页</RouterLink>
        <RouterLink to="/ai-scan">AI识花</RouterLink>
        <RouterLink to="/cart">购物车</RouterLink>
        <RouterLink to="/admin/dashboard">管理端</RouterLink>
      </nav>
    </header>
    <main>
      <RouterView />
    </main>
    <AiChatWidget />
  </div>
</template>

<script setup lang="ts">
import AiChatWidget from "@/components/AiChatWidget.vue";
</script>

<style scoped>
.topbar {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid #e5e7eb;
  background: #ffffff;
}

.brand {
  color: #1677ff;
  font-size: 24px;
  font-weight: 700;
}

nav {
  display: flex;
  gap: 16px;
}
</style>

