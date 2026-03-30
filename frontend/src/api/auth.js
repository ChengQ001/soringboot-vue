import apiClient from './axios'

/** 认证相关，与后端一致：均为 POST + JSON */
export const authApi = {
  login: (data) => apiClient.post('/auth/login', data),
  register: (data) => apiClient.post('/auth/register', data),
  updateUser: (data) => apiClient.post('/auth/update', data),
  /** POST /auth/logout，成功时 body 为 { code:200, data: true }（JWT 无状态，true 表示本次退出已受理） */
  logout: () => apiClient.post('/auth/logout', {})
}
