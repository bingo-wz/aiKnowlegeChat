import { get, post, put, del } from './index'

export interface KnowledgeBaseDTO {
  id?: number
  name: string
  description?: string
  category?: string
  tags?: string
  docCount?: number
}

export interface KnowledgeDoc {
  id: number
  kbId: number
  title: string
  content?: string
  fileUrl?: string
  fileType?: string
  fileSize?: number
  uploadedBy: number
  createdAt: string
  updatedAt: string
}

/**
 * 创建知识库
 */
export const createKnowledgeBase = (data: KnowledgeBaseDTO) =>
  post<number>('/knowledge', data)

/**
 * 获取知识库列表
 */
export const getKnowledgeBases = () =>
  get<KnowledgeBaseDTO[]>('/knowledge')

/**
 * 获取知识库详情
 */
export const getKnowledgeBaseDetail = (id: number) =>
  get<KnowledgeBaseDTO>(`/knowledge/${id}`)

/**
 * 更新知识库
 */
export const updateKnowledgeBase = (id: number, data: KnowledgeBaseDTO) =>
  put(`/knowledge/${id}`, data)

/**
 * 删除知识库
 */
export const deleteKnowledgeBase = (id: number) =>
  del(`/knowledge/${id}`)

/**
 * 上传文档到知识库
 */
export const uploadKnowledgeDoc = (id: number, file: File, title?: string) => {
  const formData = new FormData()
  formData.append('file', file)
  if (title) formData.append('title', title)
  return post<number>(`/knowledge/${id}/upload`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 获取知识库文档列表
 */
export const getKnowledgeDocs = (id: number) =>
  get<KnowledgeDoc[]>(`/knowledge/${id}/docs`)

/**
 * 删除文档
 */
export const deleteKnowledgeDoc = (docId: number) =>
  del(`/knowledge/docs/${docId}`)
