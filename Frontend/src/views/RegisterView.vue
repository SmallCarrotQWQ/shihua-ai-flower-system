<template>
  <section class="auth-page">
    <form class="auth-panel" @submit.prevent="submit">
      <p class="eyebrow">Create account</p>
      <h1>注册拾花</h1>
      <label>
        <span>用户名</span>
        <input v-model.trim="form.username" autocomplete="username" placeholder="3-32位用户名" />
      </label>
      <label>
        <span>密码</span>
        <input v-model="form.password" autocomplete="new-password" placeholder="至少6位" type="password" />
      </label>
      <label>
        <span>确认密码</span>
        <input v-model="form.confirmPassword" autocomplete="new-password" placeholder="再次输入密码" type="password" />
      </label>
      <label>
        <span>手机号</span>
        <input v-model.trim="form.phone" placeholder="可选，11位手机号" />
      </label>
      <label>
        <span>性别</span>
        <select v-model.number="form.gender">
          <option :value="0">保密</option>
          <option :value="1">男</option>
          <option :value="2">女</option>
        </select>
      </label>
      <p v-if="error" class="error">{{ error }}</p>
      <button :disabled="loading" type="submit">{{ loading ? "注册中..." : "注册并登录" }}</button>
      <RouterLink to="/login">已有账号？去登录</RouterLink>
    </form>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { register } from "@/api/auth";
import { useAuthStore } from "@/stores/auth";

const router = useRouter();
const auth = useAuthStore();
const loading = ref(false);
const error = ref("");
const form = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  phone: "",
  gender: 0
});

async function submit() {
  error.value = "";
  if (form.username.length < 3) {
    error.value = "用户名至少3位";
    return;
  }
  if (form.password.length < 6) {
    error.value = "密码至少6位";
    return;
  }
  if (form.password !== form.confirmPassword) {
    error.value = "两次输入的密码不一致";
    return;
  }

  loading.value = true;
  try {
    const { data } = await register(form);
    auth.setSession(data.data.token, data.data.userInfo);
    router.push("/home");
  } catch (exception: any) {
    error.value = exception?.response?.data?.message || "注册失败，请稍后重试";
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
  width: min(440px, 100%);
  display: grid;
  gap: 14px;
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

input,
select {
  height: 42px;
  border: 1px solid #d0d5dd;
  border-radius: 6px;
  padding: 0 12px;
  background: #ffffff;
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

