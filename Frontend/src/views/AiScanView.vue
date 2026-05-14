<template>
  <section class="page">
    <h1>AI 拍照识花</h1>
    <div class="panel">
      <ImageUploader @selected="file = $event" />
      <p class="muted">{{ file ? `已选择：${file.name}` : "模型文件放在 FastAPI-Backend/runtime/models/flower_model.h5" }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue";
import ImageUploader from "@/components/ImageUploader.vue";

const file = ref<File | null>(null);
</script>

