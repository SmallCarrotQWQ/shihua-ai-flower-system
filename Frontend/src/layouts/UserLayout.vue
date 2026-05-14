<template>
  <div>
    <header class="topbar">
      <RouterLink class="brand" to="/home">拾花</RouterLink>
      <nav>
        <RouterLink to="/home">首页</RouterLink>
        <RouterLink to="/ai-scan">AI识花</RouterLink>
        <RouterLink to="/cart">购物车</RouterLink>
        <RouterLink to="/admin/dashboard">管理端</RouterLink>
        <RouterLink v-if="!auth.token" to="/login">登录</RouterLink>
        <button v-else type="button" @click="logout">{{ auth.username }} / 退出</button>
      </nav>
    </header>
    <main>
      <RouterView />
    </main>
    <AiChatWidget />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import AiChatWidget from "@/components/AiChatWidget.vue";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();
const router = useRouter();

function logout() {
  auth.logout();
  router.push("/login");
}
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
  align-items: center;
  gap: 16px;
}

button {
  border: 0;
  background: transparent;
  color: #1677ff;
  cursor: pointer;
  font: inherit;
}
</style>
