<template>
  <section class="chat">
    <button type="button" @click="open = !open">AI</button>
    <div v-if="open" class="window">
      <strong>智能花语客服</strong>
      <p class="muted">FastAPI /chat 接口接入后在这里显示对话。</p>
      <input v-model="message" placeholder="问问鲜花养护或送花礼仪" />
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue";

const open = ref(false);
const message = ref("");
</script>

<style scoped>
.chat {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 10;
}

button {
  width: 52px;
  height: 52px;
  border: 0;
  border-radius: 50%;
  color: #ffffff;
  background: #1677ff;
  font-weight: 700;
  cursor: pointer;
}

.window {
  position: absolute;
  right: 0;
  bottom: 64px;
  width: 280px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 16px 40px rgba(16, 24, 40, 0.16);
}

input {
  width: 100%;
  height: 36px;
  border: 1px solid #d0d5dd;
  border-radius: 6px;
  padding: 0 10px;
}
</style>

