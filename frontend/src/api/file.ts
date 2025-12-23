import { get, post, del } from './index'

export interface FileUpload {
  id: number
  fileName: string
  filePath: string
  fileSize: number
  fileType: string
  uploaderId: number
  relatedType?: string
  relatedId?: number
  createdAt: string
}

/**
 * 上传文件
 */
export const uploadFile = (
  file: File,
  relatedType?: string,
  relatedId?: number
) => {
  const formData = new FormData()
  formData.append('file', file)
  if (relatedType) formData.append('relatedType', relatedType)
  if (relatedId) formData.append('relatedId', relatedId.toString())
  return post<FileUpload>('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 获取文件信息
 */
export const getFileInfo = (id: number) =>
  get<FileUpload>(`/files/info/${id}`)

/**
 * 获取文件 URL
 */
export const getFileUrl = (filePath: string) => `/api/files/${filePath}`

/**
 * 删除文件
 */
export const deleteFile = (id: number) =>
  del(`/files/${id}`)

/**
 * 获取关联文件列表
 */
export const getRelatedFiles = (relatedType: string, relatedId: number) =>
  get<FileUpload[]>('/files/related', { relatedType, relatedId })
