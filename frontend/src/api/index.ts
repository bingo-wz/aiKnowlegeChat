import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { code, message, data } = response.data
    if (code === 200) {
      return data
    } else if (code === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(new Error(message))
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service

// 通用请求方法
export const request = <T = any>(config: AxiosRequestConfig): Promise<T> => {
  return service(config)
}

export const get = <T = any>(url: string, params?: any): Promise<T> => {
  return service.get(url, { params })
}

export const post = <T = any>(url: string, data?: any): Promise<T> => {
  return service.post(url, data)
}

export const put = <T = any>(url: string, data?: any): Promise<T> => {
  return service.put(url, data)
}

export const del = <T = any>(url: string, params?: any): Promise<T> => {
  return service.delete(url, { params })
}
