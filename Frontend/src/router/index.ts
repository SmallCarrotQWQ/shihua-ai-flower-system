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
import ProfileView from "@/views/ProfileView.vue";
import AddressView from "@/views/AddressView.vue";
import OrderListView from "@/views/OrderListView.vue";
import AdminDashboard from "@/views/admin/AdminDashboard.vue";
import AdminFlowerView from "@/views/admin/AdminFlowerView.vue";
import AdminCategoryView from "@/views/admin/AdminCategoryView.vue";
import AdminOrderView from "@/views/admin/AdminOrderView.vue";
import AdminUserView from "@/views/admin/AdminUserView.vue";
import { useAuthStore } from "@/stores/auth";

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
        { path: "profile", component: ProfileView },
        { path: "address", component: AddressView },
        { path: "orders", component: OrderListView },
        { path: "login", component: LoginView },
        { path: "register", component: RegisterView }
      ]
    },
    {
      path: "/admin",
      component: AdminLayout,
      children: [
        { path: "", redirect: "/admin/dashboard" },
        { path: "dashboard", component: AdminDashboard },
        { path: "flower", component: AdminFlowerView },
        { path: "category", component: AdminCategoryView },
        { path: "order", component: AdminOrderView },
        { path: "user", component: AdminUserView }
      ]
    }
  ]
});

router.beforeEach((to) => {
  const auth = useAuthStore();
  if (to.path.startsWith("/admin") && auth.userInfo?.role !== "ADMIN") {
    return "/home";
  }
  if (["/cart", "/profile", "/address", "/orders"].includes(to.path) && !auth.token) {
    return "/login";
  }
});

export default router;
