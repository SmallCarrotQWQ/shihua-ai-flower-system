<template>
  <section class="page home">
    <div>
      <p class="eyebrow">AI Flower Shop</p>
      <h1>拾花</h1>
      <p>融合鲜花电商、拍照识花、智能贺卡和花语客服的课程实训项目骨架。</p>
    </div>
    <div class="panel">
      <h2>服务状态</h2>
      <p class="muted">{{ healthText }}</p>
      <button type="button" @click="checkHealth">检查 SpringBoot</button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { getBackendHealth } from "@/api/health";

const healthText = ref("尚未检查");

async function checkHealth() {
  try {
    const { data } = await getBackendHealth();
    healthText.value = JSON.stringify(data);
  } catch (error) {
    healthText.value = "无法连接 SpringBoot，请确认 8080 端口服务已启动。";
  }
}
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

button {
  height: 40px;
  border: 0;
  border-radius: 6px;
  padding: 0 14px;
  color: #ffffff;
  background: #1677ff;
}

@media (max-width: 760px) {
  .home {
    grid-template-columns: 1fr;
    padding: 32px 0;
  }
}
</style>

