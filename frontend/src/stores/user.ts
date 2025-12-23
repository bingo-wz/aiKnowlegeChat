import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface User {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  role: 'STUDENT' | 'TEACHER' | 'ADMIN'
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<User | null>(null)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info: User) => {
    userInfo.value = info
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  const isTeacher = () => {
    return userInfo.value?.role === 'TEACHER' || userInfo.value?.role === 'ADMIN'
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    logout,
    isTeacher
  }
})
