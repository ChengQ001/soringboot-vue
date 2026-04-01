import axios from 'axios'
import { logoutLocal } from '../utils/auth'

const apiClient = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器，添加token
apiClient.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    const parkId = localStorage.getItem('parkId')
    if (parkId) {
      config.headers['X-Park-Id'] = parkId
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器：成功返回后端 ApiResponse；失败时尽量带上 msg
apiClient.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    const status = error.response?.status
    const data = error.response?.data
    const isUnauthorized = status === 401 || data?.code === 401
    const reqUrl = error.config?.url || ''
    const isLogoutRequest = reqUrl.includes('/auth/logout')
    if (isUnauthorized && !isLogoutRequest) {
      const path = window.location.pathname || ''
      const isPublic = path === '/login' || path === '/register'
      if (!isPublic) {
        logoutLocal()
      }
    }
    if (data && typeof data.msg === 'string') {
      const e = new Error(data.msg)
      e.code = data.code
      return Promise.reject(e)
    }
    return Promise.reject(error)
  }
)

export default apiClient
