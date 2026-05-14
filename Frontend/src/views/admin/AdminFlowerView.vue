<template>
  <section>
    <h1>鲜花管理</h1>
    <form class="panel form" @submit.prevent="save">
      <input v-model.trim="form.flowerName" placeholder="鲜花名称" />
      <select v-model.number="form.categoryId">
        <option v-for="category in categories" :key="category.categoryId" :value="category.categoryId">{{ category.categoryName }}</option>
      </select>
      <input v-model.number="form.price" placeholder="价格" type="number" step="0.01" />
      <input v-model.number="form.stock" placeholder="库存" type="number" />
      <input v-model.trim="form.flowerLanguage" placeholder="花语" />
      <input v-model.trim="form.description" placeholder="描述" />
      <input v-model.trim="form.careGuide" placeholder="养护指南" />
      <button type="submit">{{ editingId ? "保存修改" : "新增鲜花" }}</button>
      <button v-if="editingId" type="button" @click="reset">取消</button>
    </form>
    <table>
      <thead>
        <tr><th>ID</th><th>名称</th><th>分类</th><th>价格</th><th>库存</th><th>销量</th><th>状态</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="item in flowers" :key="item.flowerId">
          <td>{{ item.flowerId }}</td>
          <td>{{ item.flowerName }}</td>
          <td>{{ item.categoryName }}</td>
          <td>{{ item.price }}</td>
          <td>{{ item.stock }}</td>
          <td>{{ item.salesCount }}</td>
          <td>{{ item.status === 1 ? "上架" : "下架" }}</td>
          <td>
            <button @click="edit(item)">编辑</button>
            <button @click="toggleStatus(item)">{{ item.status === 1 ? "下架" : "上架" }}</button>
            <button @click="remove(item.flowerId)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-if="message">{{ message }}</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { createAdminFlower, deleteAdminFlower, getAdminCategories, getAdminFlowers, updateAdminFlower, updateAdminFlowerStatus, type AdminFlowerPayload } from "@/api/admin";
import type { Category, Flower } from "@/api/catalog";

const flowers = ref<Flower[]>([]);
const categories = ref<Category[]>([]);
const editingId = ref<number | null>(null);
const message = ref("");
const form = reactive<AdminFlowerPayload>({
  flowerName: "",
  categoryId: 1,
  price: 0,
  stock: 0,
  coverImage: "",
  description: "",
  flowerLanguage: "",
  careGuide: "",
  status: 1
});

onMounted(load);

async function load() {
  const [flowerResult, categoryResult] = await Promise.all([getAdminFlowers(), getAdminCategories()]);
  flowers.value = flowerResult.data.data;
  categories.value = categoryResult.data.data;
  if (categories.value[0] && !form.categoryId) form.categoryId = categories.value[0].categoryId;
}

async function save() {
  if (editingId.value) {
    await updateAdminFlower(editingId.value, form);
  } else {
    await createAdminFlower(form);
  }
  reset();
  await load();
}

function edit(item: Flower) {
  editingId.value = item.flowerId;
  form.flowerName = item.flowerName;
  form.categoryId = item.categoryId;
  form.price = item.price;
  form.stock = item.stock;
  form.coverImage = item.coverImage || "";
  form.description = item.description || "";
  form.flowerLanguage = item.flowerLanguage || "";
  form.careGuide = item.careGuide || "";
  form.status = item.status;
}

async function toggleStatus(item: Flower) {
  await updateAdminFlowerStatus(item.flowerId, item.status === 1 ? 0 : 1);
  await load();
}

async function remove(id: number) {
  try {
    await deleteAdminFlower(id);
    await load();
  } catch (error: any) {
    message.value = error?.response?.data?.message || "删除失败";
  }
}

function reset() {
  editingId.value = null;
  form.flowerName = "";
  form.price = 0;
  form.stock = 0;
  form.description = "";
  form.flowerLanguage = "";
  form.careGuide = "";
  form.status = 1;
}
</script>

<style scoped>
.form {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px;
  margin-bottom: 16px;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
}

td,
th {
  border: 1px solid #e5e7eb;
  padding: 8px;
}
</style>
