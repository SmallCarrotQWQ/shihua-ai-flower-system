<template>
  <section class="page">
    <h2>个人中心</h2>
    <div v-if="auth.userInfo" class="panel">
      <label>用户名<input :value="auth.userInfo.username" disabled /></label>
      <label>手机号<input v-model="form.phone" /></label>
      <label>邮箱<input v-model="form.email" /></label>
      <label>头像<input v-model="form.avatar" /></label>
      <label>性别
        <select v-model.number="form.gender">
          <option :value="0">未设置</option>
          <option :value="1">男</option>
          <option :value="2">女</option>
        </select>
      </label>
      <button type="button" @click="saveProfile">保存资料</button>
    </div>

    <div class="panel">
      <h3>修改密码</h3>
      <label>原密码<input v-model="password.oldPassword" type="password" /></label>
      <label>新密码<input v-model="password.newPassword" type="password" /></label>
      <button type="button" @click="savePassword">修改密码</button>
    </div>

    <p v-if="message">{{ message }}</p>
    <RouterLink to="/address">管理收货地址</RouterLink>
    <RouterLink to="/orders">查看我的订单</RouterLink>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { changePassword, updateProfile } from "@/api/user";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();
const message = ref("");
const form = reactive({
  gender: auth.userInfo?.gender ?? 0,
  phone: auth.userInfo?.phone ?? "",
  email: auth.userInfo?.email ?? "",
  avatar: auth.userInfo?.avatar ?? ""
});
const password = reactive({ oldPassword: "", newPassword: "" });

async function saveProfile() {
  const response = await updateProfile(form);
  if (auth.token) {
    auth.setSession(auth.token, response.data.data);
  }
  message.value = "资料已保存";
}

async function savePassword() {
  await changePassword(password.oldPassword, password.newPassword);
  password.oldPassword = "";
  password.newPassword = "";
  message.value = "密码已修改";
}
</script>

<style scoped>
.page {
  padding: 24px;
}
.panel {
  display: grid;
  gap: 12px;
  max-width: 520px;
  margin-bottom: 20px;
}
label {
  display: grid;
  gap: 6px;
}
input,
select {
  height: 36px;
  padding: 0 10px;
}
button {
  width: 120px;
  height: 36px;
}
a {
  display: inline-block;
  margin-right: 16px;
}
</style>
