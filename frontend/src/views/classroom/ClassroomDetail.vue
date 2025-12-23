<template>
  <div class="classroom-detail">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="60px">
        <div class="menu-item active" @click="activeTab = 'chat'">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="menu-item" @click="activeTab = 'doc'">
          <el-icon><Document /></el-icon>
        </div>
        <div class="menu-item" @click="activeTab = 'file'">
          <el-icon><Folder /></el-icon>
        </div>
      </el-aside>

      <!-- 主内容区 -->
      <el-main>
        <!-- 聊天区域 -->
        <div v-show="activeTab === 'chat'" class="chat-container">
          <div class="chat-header">
            <h2>{{ classroom.name }}</h2>
            <span class="online-count">在线：{{ onlineCount }}</span>
          </div>
          <div class="chat-messages" ref="messagesRef">
            <div v-for="msg in messages" :key="msg.id" :class="['message', msg.isSelf ? 'self' : '']">
              <el-avatar :src="msg.avatar" />
              <div class="message-content">
                <span class="username">{{ msg.username }}</span>
                <div class="content">{{ msg.content }}</div>
              </div>
            </div>
          </div>
          <div class="chat-input">
            <el-input v-model="inputMessage" placeholder="输入消息..." @keyup.enter="sendMessage" />
            <el-button type="primary" @click="sendMessage">发送</el-button>
          </div>
        </div>

        <!-- 协同文档 -->
        <div v-show="activeTab === 'doc'" class="doc-container">
          <div class="doc-header">
            <h2>协同文档</h2>
            <el-button type="primary" @click="saveDoc">保存</el-button>
          </div>
          <div class="doc-editor">
            <QuillEditor v-model="docContent" />
          </div>
        </div>

        <!-- 文件中心 -->
        <div v-show="activeTab === 'file'" class="file-container">
          <div class="file-header">
            <h2>文件中心</h2>
            <el-upload action="/api/files/upload" :show-file-list="false">
              <el-button type="primary">上传文件</el-button>
            </el-upload>
          </div>
          <el-table :data="files">
            <el-table-column prop="name" label="文件名" />
            <el-table-column prop="size" label="大小" />
            <el-table-column prop="uploader" label="上传者" />
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button link type="primary" @click="downloadFile(row)">下载</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ChatDotRound, Document, Folder } from '@element-plus/icons-vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

const route = useRoute()
const activeTab = ref('chat')
const messagesRef = ref()
const classroomId = route.params.id as string

const classroom = reactive({
  name: 'Java 高级编程'
})

const onlineCount = ref(30)
const inputMessage = ref('')
const docContent = ref('')

const messages = ref([
  { id: 1, username: '张三', avatar: '', content: '大家好！', isSelf: false },
  { id: 2, username: '李四', avatar: '', content: '欢迎来到课堂', isSelf: false }
])

const files = ref([
  { name: '教学课件.pdf', size: '2.5MB', uploader: '张老师' },
  { name: '作业要求.docx', size: '156KB', uploader: '张老师' }
])

const sendMessage = () => {
  if (!inputMessage.value.trim()) return
  // TODO: 通过 WebSocket 发送消息
  messages.value.push({
    id: Date.now(),
    username: '我',
    avatar: '',
    content: inputMessage.value,
    isSelf: true
  })
  inputMessage.value = ''
  nextTick(() => {
    messagesRef.value?.scrollTo(0, messagesRef.value.scrollHeight)
  })
}

const saveDoc = () => {
  // TODO: 保存文档
}

const downloadFile = (file: any) => {
  // TODO: 下载文件
}

onMounted(() => {
  // TODO: 建立 WebSocket 连接
})
</script>

<style scoped>
.classroom-detail {
  height: 100%;
}

.el-container {
  height: 100%;
}

.el-aside {
  background: #2c3e50;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;
}

.menu-item {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
  border-radius: 8px;
  cursor: pointer;
  color: #fff;
  font-size: 20px;
}

.menu-item:hover,
.menu-item.active {
  background: #409eff;
}

.chat-container,
.doc-container,
.file-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-header,
.doc-header,
.file-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.message {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.message.self {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 60%;
}

.message .username {
  font-size: 12px;
  color: #999;
}

.message .content {
  background: #f1f2f6;
  padding: 8px 12px;
  border-radius: 8px;
  margin-top: 5px;
}

.message.self .content {
  background: #409eff;
  color: #fff;
}

.chat-input {
  display: flex;
  gap: 10px;
  padding: 15px;
  border-top: 1px solid #eee;
}

.doc-editor {
  flex: 1;
  overflow-y: auto;
}
</style>
