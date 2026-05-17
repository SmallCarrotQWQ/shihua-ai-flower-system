import type { ApiResponse } from "./auth";
import { http } from "./http";

export interface AiChatReply {
  reply: string;
  session_id: string;
  source: string;
  context_used?: string[];
}

export function chatWithAi(message: string, sessionId: string) {
  return http.post<ApiResponse<AiChatReply>>("/ai/chat", {
    message,
    sessionId
  });
}

export interface FlowerRecognitionResult {
  class_id: number;
  class_name_en: string;
  class_name_cn: string;
  confidence: number;
  flower_language: string;
  related_products: number[];
}

export async function recognizeFlower(file: File, topK = 5) {
  const baseURL = import.meta.env.VITE_AI_BASE_URL ?? "http://localhost:5000";
  const formData = new FormData();
  formData.append("image", file);
  formData.append("top_k", String(topK));

  const response = await fetch(`${baseURL}/recognize`, {
    method: "POST",
    body: formData
  });

  if (!response.ok) {
    throw new Error("AI 识花服务暂时不可用，请稍后再试。");
  }

  return response.json() as Promise<ApiResponse<{ predictions: FlowerRecognitionResult[] }>>;
}

export interface AiCard {
  cardId: number;
  flowerId?: number;
  relation: string;
  occasion: string;
  generatedContent: string;
  createTime?: string;
}

export interface AiCardPayload {
  flowerId?: number;
  flowerName: string;
  relation: string;
  occasion: string;
  style: string;
  length: number;
}

export function generateAiCard(payload: AiCardPayload) {
  return http.post<ApiResponse<AiCard>>("/ai/card", payload);
}

export function getAiCards() {
  return http.get<ApiResponse<AiCard[]>>("/ai/card");
}

export interface AiStreamDone {
  reply: string;
  session_id: string;
  source: string;
  suggestions?: string[];
}

export interface AiStreamHandlers {
  onDelta: (content: string) => void;
  onDone: (data: AiStreamDone) => void;
  onError: (message: string) => void;
}

export async function streamChatWithAi(message: string, sessionId: string, handlers: AiStreamHandlers) {
  const baseURL = import.meta.env.VITE_AI_BASE_URL ?? "http://localhost:5000";
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    Accept: "text/event-stream"
  };

  const response = await fetch(`${baseURL}/chat/stream`, {
    method: "POST",
    headers,
    body: JSON.stringify({ message, session_id: sessionId, sessionId })
  });

  if (!response.ok || !response.body) {
    handlers.onError("AI 客服暂时不可用，请稍后再试。");
    return;
  }

  const reader = response.body.getReader();
  const decoder = new TextDecoder("utf-8");
  let buffer = "";

  while (true) {
    const { value, done } = await reader.read();
    if (done) {
      break;
    }
    buffer += decoder.decode(value, { stream: true });
    const events = buffer.split("\n\n");
    buffer = events.pop() ?? "";
    for (const eventText of events) {
      handleSseEvent(eventText, handlers);
    }
  }

  if (buffer.trim()) {
    handleSseEvent(buffer, handlers);
  }
}

function handleSseEvent(eventText: string, handlers: AiStreamHandlers) {
  const dataLine = eventText
    .split("\n")
    .find((line) => line.startsWith("data:"));
  if (!dataLine) {
    return;
  }
  try {
    const payload = JSON.parse(dataLine.slice(5).trim());
    if (payload.event === "delta") {
      if (import.meta.env.DEV) {
        console.debug("[ai-stream] delta", payload.content);
      }
      handlers.onDelta(payload.content ?? "");
    }
    if (payload.event === "done") {
      handlers.onDone(payload);
    }
    if (payload.event === "error") {
      handlers.onError(payload.message ?? "AI 客服暂时不可用，请稍后再试。");
    }
  } catch {
    handlers.onError("AI 客服返回数据解析失败。");
  }
}
