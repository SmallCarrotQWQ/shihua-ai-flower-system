import { defineStore } from "pinia";
import type { UserInfo } from "@/api/auth";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("shihua_token") || "",
    username: localStorage.getItem("shihua_username") || "",
    userInfo: JSON.parse(localStorage.getItem("shihua_user_info") || "null") as UserInfo | null
  }),
  actions: {
    setSession(token: string, userInfo: UserInfo) {
      this.token = token;
      this.username = userInfo.username;
      this.userInfo = userInfo;
      localStorage.setItem("shihua_token", token);
      localStorage.setItem("shihua_username", userInfo.username);
      localStorage.setItem("shihua_user_info", JSON.stringify(userInfo));
    },
    logout() {
      this.token = "";
      this.username = "";
      this.userInfo = null;
      localStorage.removeItem("shihua_token");
      localStorage.removeItem("shihua_username");
      localStorage.removeItem("shihua_user_info");
    }
  }
});
