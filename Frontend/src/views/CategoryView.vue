<template>
  <section class="page">
    <h1>鲜花分类</h1>
    <div class="panel list">
      <RouterLink v-for="category in categories" :key="category.categoryId" :to="`/list?categoryId=${category.categoryId}`">
        {{ category.categoryName }}
      </RouterLink>
    </div>
    <p v-if="error" class="error">{{ error }}</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { getCategories, type Category } from "@/api/catalog";

const categories = ref<Category[]>([]);
const error = ref("");

onMounted(async () => {
  try {
    const { data } = await getCategories();
    categories.value = data.data;
  } catch {
    error.value = "分类加载失败";
  }
});
</script>

<style scoped>
h1 {
  margin-top: 28px;
}

.list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 12px;
}

a {
  padding: 14px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: #ffffff;
}

.error {
  color: #d92d20;
}
</style>

