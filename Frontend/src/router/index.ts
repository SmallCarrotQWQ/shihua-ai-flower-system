import { createRouter, createWebHistory } from "vue-router";
import UserLayout from "@/layouts/UserLayout.vue";
import AdminLayout from "@/layouts/AdminLayout.vue";
import HomeView from "@/views/HomeView.vue";
import AiScanView from "@/views/AiScanView.vue";
import CartView from "@/views/CartView.vue";
import CategoryView from "@/views/CategoryView.vue";
import FlowerListView from "@/views/FlowerListView.vue";
import FlowerDetailView from "@/views/FlowerDetailView.vue";
import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import AdminDashboard from "@/views/admin/AdminDashboard.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      component: UserLayout,
      children: [
        { path: "", redirect: "/home" },
        { path: "home", component: HomeView },
        { path: "category", component: CategoryView },
        { path: "list", component: FlowerListView },
        { path: "detail/:id", component: FlowerDetailView },
        { path: "ai-scan", component: AiScanView },
        { path: "cart", component: CartView },
        { path: "login", component: LoginView },
        { path: "register", component: RegisterView }
      ]
    },
    {
      path: "/admin",
      component: AdminLayout,
      children: [
        { path: "", redirect: "/admin/dashboard" },
        { path: "dashboard", component: AdminDashboard }
      ]
    }
  ]
});

export default router;
