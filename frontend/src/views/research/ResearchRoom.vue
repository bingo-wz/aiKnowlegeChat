<template>
  <div class="research-room">
    <!-- 侧边栏：历史对话 -->
    <el-aside width="280px" class="sidebar">
      <div class="sidebar-header">
        <h3>对话历史</h3>
        <el-button circle size="small" @click="createNewChat">
          <el-icon><Plus /></el-icon>
        </el-button>
      </div>
      <div class="knowledge-selector">
        <el-select v-model="selectedKbId" placeholder="选择知识库" style="width: 100%">
          <el-option label="全部" :value="null" />
          <el-option v-for="kb in knowledgeBases" :key="kb.id" :label="kb.name" :value="kb.id" />
        </el-select>
      </div>
      <div class="chat-list">
        <div
          v-for="chat in chatHistory"
          :key="chat.id"
          :class="['chat-item', { active: currentChatId === chat.id }]"
          @click="switchChat(chat.id)"
        >
          <div class="chat-title">{{ chat.title }}</div>
          <div class="chat-time">{{ formatTime(chat.updatedAt) }}</div>
        </div>
      </div>
    </el-aside>

    <!-- 主对话区域 -->
    <el-main class="chat-area">
      <div class="messages" ref="messagesRef">
        <div v-for="msg in messages" :key="msg.id" :class="['message', msg.role]">
          <div class="avatar">
            <el-icon v-if="msg.role === 'user'"><User /></el-icon>
            <el-icon v-else><Service /></el-icon>
          </div>
          <div class="content">
            <div class="markdown" v-html="renderMarkdown(msg.content)"></div>
          </div>
        </div>
        <div v-if="loading" class="message assistant">
          <div class="avatar"><el-icon><Service /></el-icon></div>
          <div class="content">
            <el-skeleton :rows="3" animated />
          </div>
        </div>
      </div>
      <div class="input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="输入问题..."
          @keydown.enter.ctrl="sendMessage"
        />
        <div class="input-actions">
          <el-button size="small" @click="clearChat">清空</el-button>
          <el-button type="primary" @click="sendMessage" :loading="loading">
            发送 (Ctrl+Enter)
          </el-button>
        </div>
      </div>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { Plus, User, Service } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const messagesRef = ref()
const currentChatId = ref<number | null>(null)
const selectedKbId = ref<number | null>(null)
const inputMessage = ref('')
const loading = ref(false)

const knowledgeBases = ref([
  { id: 1, name: 'Java 教程' },
  { id: 2, name: 'Spring 指南' },
  { id: 3, name: '算法与数据结构' }
])

const chatHistory = ref([
  { id: 1, title: '关于 Spring Boot 的问题', updatedAt: new Date() },
  { id: 2, title: 'HashMap 源码分析', updatedAt: new Date(Date.now() - 86400000) }
])

const messages = ref([
  {
    id: 1,
    role: 'user',
    content: '你好，介绍一下 Spring Boot 的自动配置原理'
  },
  {
    id: 2,
    role: 'assistant',
    content: 'Spring Boot 的自动配置是其核心特性之一...\n\n1. **@EnableAutoConfiguration** 注解\n2. **spring.factories** 配置文件\n3. **条件注解** (@ConditionalOnClass 等)'
  }
])

const formatTime = (date: Date) => {
  return dayjs(date).format('MM-DD HH:mm')
}

const renderMarkdown = (content: string) => {
  // TODO: 使用 marked 或 markdown-it 渲染
  return content.replace(/\n/g, '<br>')
}

const createNewChat = () => {
  currentChatId.value = Date.now()
  messages.value = []
}

const switchChat = (id: number) => {
  currentChatId.value = id
  // TODO: 加载对话历史
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return

  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: inputMessage.value
  })

  const userMessage = inputMessage.value
  inputMessage.value = ''
  loading.value = true

  nextTick(() => {
    messagesRef.value?.scrollTo(0, messagesRef.value.scrollHeight)
  })

  // TODO: 调用后端 AI 接口
  setTimeout(() => {
    messages.value.push({
      id: Date.now(),
      role: 'assistant',
      content: `这是针对 "${userMessage}" 的回答...`
    })
    loading.value = false
    nextTick(() => {
      messagesRef.value?.scrollTo(0, messagesRef.value.scrollHeight)
    })
  }, 1000)
}

const clearChat = () => {
  messages.value = []
}

onMounted(() => {
  createNewChat()
})
</script>

<style scoped>
.research-room {
  height: 100%;
  display: flex;
}

.sidebar {
  background: #f5f7fa;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.knowledge-selector {
  padding: 10px 15px;
  border-bottom: 1px solid #e4e7ed;
}

.chat-list {
  flex: 1;
  overflow-y: auto;
}

.chat-item {
  padding: 12px 15px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
}

.chat-item:hover {
  background: #e9ecf0;
}

.chat-item.active {
  background: #e6f7ff;
  border-left: 3px solid #409eff;
}

.chat-title {
  font-size: 14px;
  margin-bottom: 5px;
}

.chat-time {
  font-size: 12px;
  color: #999;
}

.chat-area {
  display: flex;
  flex-direction: column;
  padding: 0;
}

.messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.message {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.message.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message.user .avatar {
  background: #409eff;
  color: #fff;
}

.message.assistant .avatar {
  background: #67c23a;
  color: #fff;
}

.content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 8px;
  background: #f1f2f6;
}

.message.user .content {
  background: #409eff;
  color: #fff;
}

.markdown {
  line-height: 1.6;
}

.input-area {
  padding: 15px 20px;
  border-top: 1px solid #e4e7ed;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}
</style>
