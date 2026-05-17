<template>
  <section class="page ai-card">
    <header class="page-head">
      <p class="eyebrow">AI Blessing Card</p>
      <h1>AI 贺卡</h1>
    </header>

    <div class="workspace">
      <form class="panel card-form" @submit.prevent="submit">
        <label>鲜花名称<input v-model.trim="form.flowerName" placeholder="如：康乃馨" /></label>
        <label>鲜花 ID（可选）<input v-model.number="form.flowerId" type="number" min="1" /></label>
        <label>收礼关系<input v-model.trim="form.relation" placeholder="如：妈妈、朋友、恋人" /></label>
        <label>场景<input v-model.trim="form.occasion" placeholder="如：生日、纪念日、探望" /></label>
        <label>风格
          <select v-model="form.style">
            <option>温暖</option>
            <option>浪漫</option>
            <option>真诚</option>
            <option>活泼</option>
            <option>正式</option>
          </select>
        </label>
        <label>字数<input v-model.number="form.length" type="number" min="10" max="200" /></label>
        <button type="submit" :disabled="loading || !canSubmit">
          {{ loading ? "生成中..." : "生成贺卡" }}
        </button>
        <p v-if="error" class="error">{{ error }}</p>
      </form>

      <div class="panel">
        <h2>生成结果</h2>
        <p v-if="latest" class="generated">{{ latest.generatedContent }}</p>
        <p v-else class="muted">填写信息后生成一段可直接写入贺卡的祝福文案。</p>
      </div>
    </div>

    <div class="panel history">
      <h2>历史贺卡</h2>
      <p v-if="!cards.length" class="muted">暂无历史贺卡。</p>
      <article v-for="card in cards" :key="card.cardId">
        <div>
          <strong>{{ card.relation }} / {{ card.occasion }}</strong>
          <span>{{ card.createTime ?? "" }}</span>
        </div>
        <p>{{ card.generatedContent }}</p>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { generateAiCard, getAiCards, type AiCard } from "@/api/ai";

const loading = ref(false);
const error = ref("");
const latest = ref<AiCard | null>(null);
const cards = ref<AiCard[]>([]);
const form = reactive({
  flowerName: "",
  flowerId: undefined as number | undefined,
  relation: "",
  occasion: "",
  style: "温暖",
  length: 50
});

const canSubmit = computed(() => form.flowerName && form.relation && form.occasion && form.length >= 10);

onMounted(loadCards);

async function loadCards() {
  try {
    const response = await getAiCards();
    cards.value = response.data.data;
  } catch {
    cards.value = [];
  }
}

async function submit() {
  if (!canSubmit.value || loading.value) {
    return;
  }
  loading.value = true;
  error.value = "";
  try {
    const response = await generateAiCard({
      flowerName: form.flowerName,
      flowerId: form.flowerId || undefined,
      relation: form.relation,
      occasion: form.occasion,
      style: form.style,
      length: form.length
    });
    latest.value = response.data.data;
    await loadCards();
  } catch {
    error.value = "AI 贺卡暂时生成失败，请稍后再试。";
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.ai-card {
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
h2,
p {
  margin: 0;
}

.workspace {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 20px;
  align-items: start;
  margin-bottom: 20px;
}

.card-form {
  display: grid;
  gap: 12px;
}

label {
  display: grid;
  gap: 6px;
  color: #344054;
}

input,
select {
  height: 38px;
  border: 1px solid #d0d5dd;
  border-radius: 6px;
  padding: 0 10px;
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

.generated {
  margin-top: 12px;
  white-space: pre-wrap;
  line-height: 1.8;
}

.error {
  color: #d92d20;
}

.history {
  display: grid;
  gap: 12px;
}

article {
  display: grid;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

article div {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

article span {
  color: #667085;
  font-size: 13px;
}

@media (max-width: 760px) {
  .workspace {
    grid-template-columns: 1fr;
  }
}
</style>
