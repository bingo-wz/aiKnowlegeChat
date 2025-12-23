import { post, get } from './index'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  nickname: string
  email?: string
}

export interface LoginResponse {
  token: string
  userId: number
  username: string
  nickname: string
  avatar: string
  role: string
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  role: string
}

/**
 * 用户登录
 */
export const login = (data: LoginRequest) => post<LoginResponse>('/auth/login', data)

/**
 * 用户注册
 */
export const register = (data: RegisterRequest) => post('/auth/register', data)

/**
 * 获取当前用户信息
 */
export const getCurrentUser = () => get<UserInfo>('/auth/me')

/**
 * 退出登录
 */
export const logout = () => post('/auth/logout')
