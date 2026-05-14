import { http } from "./http";

export function getBackendHealth() {
  return http.get("/health");
}

