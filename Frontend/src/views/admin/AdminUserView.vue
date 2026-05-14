<template>
  <section>
    <h1>用户管理</h1>
    <table>
      <thead>
        <tr><th>ID</th><th>用户名</th><th>手机</th><th>角色</th><th>状态</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.userId">
          <td>{{ user.userId }}</td>
          <td>{{ user.username }}</td>
          <td>{{ user.phone }}</td>
          <td>{{ user.role }}</td>
          <td>{{ user.status === 1 ? "正常" : "禁用" }}</td>
          <td>
            <button @click="setStatus(user.userId, user.status === 1 ? 0 : 1)">
              {{ user.status === 1 ? "禁用" : "启用" }}
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { getAdminUsers, updateAdminUserStatus } from "@/api/admin";
import type { UserInfo } from "@/api/auth";

const users = ref<UserInfo[]>([]);

onMounted(load);

async function load() {
  const { data } = await getAdminUsers();
  users.value = data.data;
}

async function setStatus(id: number, status: number) {
  await updateAdminUserStatus(id, status);
  await load();
}
</script>

<style scoped>
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

