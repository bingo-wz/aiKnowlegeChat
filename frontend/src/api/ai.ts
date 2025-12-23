import { get, post, del } from './index'

export interface ChatRequest {
  conversationId?: number
  kbId?: number
  content: string
  model?: string
}

export interface ChatResponse {
  conversationId: number
  title: string
  content: string
  referencedDocs?: string
  tokens?: number
}

export interface Conversation {
  id: number
  userId: number
  kbId?: number
  title: string
  model: string
  isPinned: boolean
  createdAt: string
  updatedAt: string
}

export interface ConversationMessage {
  id: number
  conversationId: number
  role: 'USER' | 'ASSISTANT' | 'SYSTEM'
  content: string
  referencedDocs?: string
  tokens?: number
  createdAt: string
}

/**
 * 发送消息 (AI 对话)
 */
export const sendAiMessage = (data: ChatRequest) =>
  post<ChatResponse>('/ai/chat', data)

/**
 * 获取对话列表
 */
export const getConversations = () =>
  get<Conversation[]>('/ai/conversations')

/**
 * 获取对话历史
 */
export const getConversationMessages = (id: number) =>
  get<ConversationMessage[]>(`/ai/conversations/${id}/messages`)

/**
 * 删除对话
 */
export const deleteConversation = (id: number) =>
  del(`/ai/conversations/${id}`)
