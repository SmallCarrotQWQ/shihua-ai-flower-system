<template>
  <label class="uploader">
    <input type="file" accept="image/*" @change="onChange" />
    <span>{{ fileName || "选择图片" }}</span>
  </label>
</template>

<script setup lang="ts">
import { ref } from "vue";

const emit = defineEmits<{ selected: [file: File] }>();
const fileName = ref("");

function onChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;
  fileName.value = file.name;
  emit("selected", file);
}
</script>

<style scoped>
.uploader {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 160px;
  height: 44px;
  border: 1px dashed #98a2b3;
  border-radius: 8px;
  background: #ffffff;
  cursor: pointer;
}

input {
  display: none;
}
</style>

