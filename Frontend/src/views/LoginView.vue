<template>
  <section class="auth-page">
    <form class="auth-panel" @submit.prevent="submit">
      <p class="eyebrow">Welcome back</p>
      <h1>登录拾花</h1>
      <label>
        <span>用户名</span>
        <input v-model.trim="form.username" autocomplete="username" placeholder="请输入用户名" />
      </label>
      <label>
        <span>密码</span>
        <input v-model="form.password" autocomplete="current-password" placeholder="请输入密码" type="password" />
      </label>
      <p v-if="error" class="error">{{ error }}</p>
      <button :disabled="loading" type="submit">{{ loading ? "登录中..." : "登录" }}</button>
      <RouterLink to="/register">还没有账号？去注册</RouterLink>
    </form>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { login } from "@/api/auth";
import { useAuthStore } from "@/stores/auth";

const router = useRouter();
const auth = useAuthStore();
const loading = ref(false);
const error = ref("");
const form = reactive({
  username: "",
  password: ""
});

async function submit() {
  error.value = "";
  if (!form.username || !form.password) {
    error.value = "请填写用户名和密码";
    return;
  }

  loading.value = true;
  try {
    const { data } = await login(form);
    auth.setSession(data.data.token, data.data.userInfo);
    router.push("/home");
  } catch (exception: any) {
    error.value = exception?.response?.data?.message || "登录失败，请稍后重试";
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.auth-page {
  min-height: calc(100vh - 64px);
  display: grid;
  place-items: center;
  padding: 32px 16px;
}

.auth-panel {
  width: min(420px, 100%);
  display: grid;
  gap: 16px;
  padding: 28px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #ffffff;
}

.eyebrow {
  margin: 0;
  color: #1677ff;
  font-weight: 700;
}

h1 {
  margin: 0;
  font-size: 28px;
}

label {
  display: grid;
  gap: 8px;
  font-weight: 600;
}

input {
  height: 42px;
  border: 1px solid #d0d5dd;
  border-radius: 6px;
  padding: 0 12px;
  font: inherit;
}

button {
  height: 44px;
  border: 0;
  border-radius: 6px;
  color: #ffffff;
  background: #1677ff;
  font-weight: 700;
  cursor: pointer;
}

button:disabled {
  opacity: 0.65;
}

.error {
  margin: 0;
  color: #d92d20;
}

a {
  color: #1677ff;
  text-align: center;
}
</style>

