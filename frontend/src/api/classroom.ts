import { get, post, put, del } from './index'

export interface ClassroomDTO {
  id?: number
  name: string
  description?: string
  coverImage?: string
  maxMembers?: number
  currentMembers?: number
  status?: number
}

export interface ClassroomListResponse {
  records: ClassroomDTO[]
  total: number
  current: number
  size: number
}

/**
 * 获取课堂列表
 */
export const getClassroomList = (page = 1, size = 10) =>
  get<ClassroomListResponse>('/classroom', { page, size })

/**
 * 创建课堂
 */
export const createClassroom = (data: ClassroomDTO) =>
  post<number>('/classroom', data)

/**
 * 获取课堂详情
 */
export const getClassroomDetail = (id: number) =>
  get<ClassroomDTO>(`/classroom/${id}`)

/**
 * 更新课堂
 */
export const updateClassroom = (id: number, data: ClassroomDTO) =>
  put(`/classroom/${id}`, data)

/**
 * 删除课堂
 */
export const deleteClassroom = (id: number) =>
  del(`/classroom/${id}`)

/**
 * 加入课堂
 */
export const joinClassroom = (id: number) =>
  post(`/classroom/${id}/join`)

/**
 * 离开课堂
 */
export const leaveClassroom = (id: number) =>
  post(`/classroom/${id}/leave`)
