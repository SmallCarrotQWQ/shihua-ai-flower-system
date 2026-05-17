<template>
  <section class="page ai-scan">
    <header class="page-head">
      <p class="eyebrow">AI Flower Recognition</p>
      <h1>AI 图片识花</h1>
    </header>

    <div class="workspace">
      <div class="panel upload-panel">
        <ImageUploader @selected="onSelected" />
        <p class="muted">{{ file ? `已选择：${file.name}` : "请选择一张清晰的花朵图片" }}</p>
        <img v-if="previewUrl" :src="previewUrl" alt="待识别花朵预览" class="preview" />
        <button type="button" :disabled="!file || loading" @click="recognize">
          {{ loading ? "识别中..." : "开始识别" }}
        </button>
        <p v-if="error" class="error">{{ error }}</p>
      </div>

      <div class="panel result-panel">
        <h2>识别结果</h2>
        <p v-if="!results.length && !loading" class="muted">上传图片后会展示 Top 5 预测结果。</p>
        <article v-for="item in results" :key="`${item.class_id}-${item.class_name_en}`" class="result-item">
          <div>
            <strong>{{ item.class_name_cn }}</strong>
            <span>{{ item.class_name_en }}</span>
          </div>
          <div class="confidence">
            <span>{{ toPercent(item.confidence) }}</span>
            <progress :value="item.confidence" max="1" />
          </div>
          <p>{{ item.flower_language }}</p>
          <p v-if="item.related_products.length" class="muted">
            关联商品：{{ item.related_products.join("、") }}
          </p>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { recognizeFlower, type FlowerRecognitionResult } from "@/api/ai";
import ImageUploader from "@/components/ImageUploader.vue";

const file = ref<File | null>(null);
const previewUrl = ref("");
const loading = ref(false);
const error = ref("");
const results = ref<FlowerRecognitionResult[]>([]);

function onSelected(selected: File) {
  file.value = selected;
  error.value = "";
  results.value = [];
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value);
  }
  previewUrl.value = URL.createObjectURL(selected);
}

async function recognize() {
  if (!file.value || loading.value) {
    return;
  }
  loading.value = true;
  error.value = "";
  try {
    const response = await recognizeFlower(file.value, 5);
    results.value = response.data.predictions;
    if (!results.value.length) {
      error.value = "暂未识别出结果，请换一张更清晰的花朵图片。";
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : "AI 识花服务暂时不可用，请稍后再试。";
    results.value = [];
  } finally {
    loading.value = false;
  }
}

function toPercent(value: number) {
  return `${Math.round(value * 1000) / 10}%`;
}
</script>

<style scoped>
.ai-scan {
  padding: 24px 0 40px;
}

.page-head {
  margin-bottom: 18px;
}

.eyebrow {
  margin: 0 0 6px;
  color: #1677ff;
  font-weight: 700;
}

h1,
h2 {
  margin: 0;
}

.workspace {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 20px;
  align-items: start;
}

.upload-panel,
.result-panel {
  display: grid;
  gap: 14px;
}

.preview {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

button {
  height: 40px;
  border: 0;
  border-radius: 6px;
  color: #ffffff;
  background: #1677ff;
  cursor: pointer;
}

button:disabled {
  background: #98a2b3;
  cursor: not-allowed;
}

.error {
  margin: 0;
  color: #d92d20;
}

.result-item {
  display: grid;
  gap: 8px;
  padding: 14px 0;
  border-top: 1px solid #e5e7eb;
}

.result-item div:first-child {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.result-item span {
  color: #667085;
}

.confidence {
  display: grid;
  grid-template-columns: 64px 1fr;
  align-items: center;
  gap: 10px;
}

progress {
  width: 100%;
  height: 10px;
}

p {
  margin: 0;
}

@media (max-width: 760px) {
  .workspace {
    grid-template-columns: 1fr;
  }
}
</style>

