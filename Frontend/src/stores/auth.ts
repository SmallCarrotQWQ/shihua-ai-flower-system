import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("shihua_token") || "",
    username: localStorage.getItem("shihua_username") || ""
  }),
  actions: {
    setSession(token: string, username: string) {
      this.token = token;
      this.username = username;
      localStorage.setItem("shihua_token", token);
      localStorage.setItem("shihua_username", username);
    },
    logout() {
      this.token = "";
      this.username = "";
      localStorage.removeItem("shihua_token");
      localStorage.removeItem("shihua_username");
    }
  }
});

