import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { io, Socket } from 'socket.io-client'

export interface ChatMessage {
  id?: number
  classroomId: string
  userId: string
  username: string
  avatar: string
  content: string
  type: 'TEXT' | 'IMAGE' | 'FILE'
  fileUrl?: string
  createdAt?: Date
}

export const useWebSocketStore = defineStore('websocket', () => {
  const socket = ref<Socket | null>(null)
  const connected = ref(false)
  const currentClassroom = ref<string | null>(null)

  // 连接 WebSocket
  const connect = (classroomId: string, token: string) => {
    if (socket.value?.connected) {
      disconnect()
    }

    socket.value = io({
      path: '/ws',
      auth: { token },
      query: { classroomId },
      reconnection: true,
      reconnectionDelay: 1000,
      reconnectionAttempts: 5
    })

    socket.value.on('connect', () => {
      connected.value = true
      currentClassroom.value = classroomId
      console.log('WebSocket 连接成功')
    })

    socket.value.on('disconnect', () => {
      connected.value = false
      console.log('WebSocket 连接断开')
    })

    socket.value.on('error', (error) => {
      console.error('WebSocket 错误:', error)
    })
  }

  // 断开连接
  const disconnect = () => {
    if (socket.value) {
      socket.value.disconnect()
      socket.value = null
      connected.value = false
      currentClassroom.value = null
    }
  }

  // 加入课堂
  const joinClassroom = (classroomId: string) => {
    if (socket.value?.connected) {
      socket.value.emit('join', { classroomId })
    }
  }

  // 离开课堂
  const leaveClassroom = (classroomId: string) => {
    if (socket.value?.connected) {
      socket.value.emit('leave', { classroomId })
    }
  }

  // 发送聊天消息
  const sendMessage = (message: ChatMessage) => {
    if (socket.value?.connected) {
      socket.value.emit('message', message)
    }
  }

  // 监听聊天消息
  const onMessage = (callback: (message: ChatMessage) => void) => {
    if (socket.value) {
      socket.value.on('message', callback)
    }
  }

  // 监听用户加入
  const onUserJoin = (callback: (user: any) => void) => {
    if (socket.value) {
      socket.value.on('user:join', callback)
    }
  }

  // 监听用户离开
  const onUserLeave = (callback: (userId: string) => void) => {
    if (socket.value) {
      socket.value.on('user:leave', callback)
    }
  }

  // 监听协同文档更新
  const onDocUpdate = (callback: (update: any) => void) => {
    if (socket.value) {
      socket.value.on('doc:update', callback)
    }
  }

  // 发送文档更新
  const sendDocUpdate = (classroomId: string, operation: any) => {
    if (socket.value?.connected) {
      socket.value.emit('doc:update', { classroomId, operation })
    }
  }

  return {
    socket,
    connected,
    currentClassroom,
    connect,
    disconnect,
    joinClassroom,
    leaveClassroom,
    sendMessage,
    onMessage,
    onUserJoin,
    onUserLeave,
    onDocUpdate,
    sendDocUpdate
  }
})
