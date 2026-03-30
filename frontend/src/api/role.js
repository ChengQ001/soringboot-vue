import apiClient from './axios'

/** 角色：后端全部为 POST */
export const roleApi = {
  create: (data) => apiClient.post('/roles', data),
  update: (data) => apiClient.post('/roles/update', data),
  remove: (data) => apiClient.post('/roles/delete', data),
  detail: (data) => apiClient.post('/roles/detail', data),
  list: (data = {}) => apiClient.post('/roles/list', data)
}
