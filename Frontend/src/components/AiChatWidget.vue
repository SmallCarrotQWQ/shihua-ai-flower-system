<template>
  <section class="chat">
    <button type="button" class="toggle" @click="open = !open">AI</button>
    <div v-if="open" class="window">
      <header>
        <strong>智能花语客服</strong>
        <button type="button" class="close" @click="open = false">×</button>
      </header>

      <div class="messages">
        <div
          v-for="(item, index) in messages"
          :key="index"
          :class="['message-block', item.role]"
        >
          <p :class="['message', item.role]">{{ item.content }}</p>
          <div v-if="item.role === 'assistant' && item.suggestions?.length" class="suggestions">
            <button
              v-for="suggestion in item.suggestions"
              :key="suggestion"
              type="button"
              @click="send(suggestion)"
            >
              {{ suggestion }}
            </button>
          </div>
        </div>
        <p v-if="loading && !streamingMessageVisible" class="message assistant">正在思考...</p>
      </div>

      <form @submit.prevent="send()">
        <input v-model.trim="message" placeholder="问问花语、养护或送花建议" />
        <button type="submit" :disabled="loading || !message">发送</button>
      </form>

      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { streamChatWithAi } from "@/api/ai";

interface ChatMessage {
  role: "user" | "assistant";
  content: string;
  suggestions?: string[];
}

const open = ref(false);
const message = ref("");
const loading = ref(false);
const error = ref("");
const streamingMessageVisible = ref(false);
const messages = ref<ChatMessage[]>([
  {
    role: "assistant",
    content: "你好，我是拾花 AI 客服。可以问我花语、送花建议和鲜花养护。"
  }
]);

const sessionId = getSessionId();

async function send(preset?: string) {
  const content = (preset ?? message.value).trim();
  if (!content || loading.value) {
    return;
  }
  message.value = "";
  error.value = "";
  messages.value.push({ role: "user", content });
  const assistantIndex = messages.value.length;
  messages.value.push({ role: "assistant", content: "" });
  streamingMessageVisible.value = true;
  loading.value = true;

  try {
    await streamChatWithAi(content, sessionId, {
      onDelta: (delta) => {
        updateAssistantMessage(assistantIndex, {
          content: messages.value[assistantIndex].content + delta
        });
      },
      onDone: (data) => {
        if (!messages.value[assistantIndex].content) {
          updateAssistantMessage(assistantIndex, { content: data.reply });
        }
        updateAssistantMessage(assistantIndex, {
          suggestions: (data.suggestions ?? []).slice(0, 2)
        });
      },
      onError: (message) => {
        error.value = message;
        if (!messages.value[assistantIndex].content) {
          updateAssistantMessage(assistantIndex, {
            content: "AI 客服暂时不可用，请稍后再试。"
          });
        }
      }
    });
  } catch {
    error.value = "AI 客服暂时不可用，请稍后再试。";
    updateAssistantMessage(assistantIndex, {
      content: "AI 客服暂时不可用，请稍后再试。"
    });
  } finally {
    loading.value = false;
    streamingMessageVisible.value = false;
  }
}

function updateAssistantMessage(index: number, patch: Partial<ChatMessage>) {
  const current = messages.value[index];
  if (!current) {
    return;
  }
  messages.value.splice(index, 1, {
    ...current,
    ...patch
  });
}

function getSessionId() {
  const key = "shihua_ai_chat_session";
  const existing = window.localStorage.getItem(key);
  if (existing) {
    return existing;
  }
  const created = `web_${Date.now()}_${Math.random().toString(16).slice(2)}`;
  window.localStorage.setItem(key, created);
  return created;
}
</script>

<style scoped>
.chat {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 10;
}

.toggle {
  width: 52px;
  height: 52px;
  border: 0;
  border-radius: 50%;
  color: #ffffff;
  background: #1677ff;
  font-weight: 700;
  cursor: pointer;
}

.window {
  position: absolute;
  right: 0;
  bottom: 64px;
  width: 320px;
  padding: 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 16px 40px rgba(16, 24, 40, 0.16);
}

header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.close {
  border: 0;
  background: transparent;
  color: #667085;
  font-size: 20px;
  cursor: pointer;
}

.messages {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 280px;
  overflow-y: auto;
  padding-right: 4px;
}

.message-block {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.message-block.assistant {
  align-items: flex-start;
}

.message-block.user {
  align-items: flex-end;
}

.message {
  margin: 0;
  padding: 8px 10px;
  border-radius: 8px;
  line-height: 1.5;
  word-break: break-word;
}

.message.assistant {
  align-self: flex-start;
  background: #f2f4f7;
  color: #101828;
}

.message.user {
  background: #1677ff;
  color: #ffffff;
}

.suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  max-width: 100%;
}

.suggestions button {
  border: 1px solid #b2ddff;
  border-radius: 999px;
  background: #eff8ff;
  color: #175cd3;
  padding: 5px 9px;
  cursor: pointer;
  font-size: 12px;
}

form {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

input {
  flex: 1;
  min-width: 0;
  width: 100%;
  height: 36px;
  border: 1px solid #d0d5dd;
  border-radius: 6px;
  padding: 0 10px;
}

form button {
  width: 64px;
  border: 0;
  border-radius: 6px;
  color: #ffffff;
  background: #1677ff;
  cursor: pointer;
}

form button:disabled {
  background: #98a2b3;
  cursor: not-allowed;
}

.error {
  margin: 8px 0 0;
  color: #d92d20;
  font-size: 13px;
}
</style>
