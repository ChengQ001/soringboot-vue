import apiClient from './axios'

/** 用户模块：均为 POST + JSON（路径与后端 UserController 一致） */
export const userApi = {
  /** 用户列表（需登录） */
  getUsers: (data = {}) => apiClient.post('/users', data),

  getUsersAnonymous: (data = {}) => apiClient.post('/users/anonymous', data),
  getUsersPermitAll: (data = {}) => apiClient.post('/users/permit-all', data),

  getCurrentUser: (data = {}) => apiClient.post('/users/current-user', data),

  createUser: (data) => apiClient.post('/users/create', data),
  deleteUser: (data) => apiClient.post('/users/delete', data)
}
