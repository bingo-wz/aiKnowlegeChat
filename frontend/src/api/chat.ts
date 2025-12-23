import { get, post } from './index'

export interface ChatMessageDTO {
  id?: number
  classroomId: number
  userId: number
  username: string
  avatar: string
  messageType: string
  content: string
  fileUrl?: string
  fileName?: string
  createdAt?: string
}

export interface ChatHistoryResponse {
  records: ChatMessageDTO[]
  total: number
}

/**
 * 发送消息
 */
export const sendMessage = (data: {
  classroomId: number
  messageType?: string
  content: string
  fileUrl?: string
  fileName?: string
}) => post('/chat/send', data)

/**
 * 获取聊天历史
 */
export const getChatHistory = (classroomId: number, page = 1, size = 20) =>
  get<ChatHistoryResponse>(`/chat/history/${classroomId}`, { page, size })

/**
 * 获取在线人数
 */
export const getOnlineCount = (classroomId: number) =>
  get<number>(`/chat/online/${classroomId}`)
