<template>
  <section>
    <h1>分类管理</h1>
    <form class="panel form" @submit.prevent="save">
      <input v-model.trim="form.categoryName" placeholder="分类名称" />
      <input v-model.number="form.sortOrder" placeholder="排序" type="number" />
      <button type="submit">{{ editingId ? "保存修改" : "新增分类" }}</button>
      <button v-if="editingId" type="button" @click="reset">取消</button>
    </form>
    <table>
      <thead>
        <tr><th>ID</th><th>名称</th><th>排序</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="item in categories" :key="item.categoryId">
          <td>{{ item.categoryId }}</td>
          <td>{{ item.categoryName }}</td>
          <td>{{ item.sortOrder }}</td>
          <td>
            <button @click="edit(item)">编辑</button>
            <button @click="remove(item.categoryId)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-if="message">{{ message }}</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { createAdminCategory, deleteAdminCategory, getAdminCategories, updateAdminCategory, type AdminCategoryPayload } from "@/api/admin";
import type { Category } from "@/api/catalog";

const categories = ref<Category[]>([]);
const editingId = ref<number | null>(null);
const message = ref("");
const form = reactive<AdminCategoryPayload>({ categoryName: "", parentId: 0, sortOrder: 0 });

onMounted(load);

async function load() {
  const { data } = await getAdminCategories();
  categories.value = data.data;
}

async function save() {
  if (editingId.value) {
    await updateAdminCategory(editingId.value, form);
  } else {
    await createAdminCategory(form);
  }
  reset();
  await load();
}

function edit(item: Category) {
  editingId.value = item.categoryId;
  form.categoryName = item.categoryName;
  form.parentId = item.parentId;
  form.sortOrder = item.sortOrder;
}

async function remove(id: number) {
  try {
    await deleteAdminCategory(id);
    await load();
  } catch (error: any) {
    message.value = error?.response?.data?.message || "删除失败";
  }
}

function reset() {
  editingId.value = null;
  form.categoryName = "";
  form.parentId = 0;
  form.sortOrder = 0;
}
</script>

<style scoped>
.form {
  display: flex;
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

