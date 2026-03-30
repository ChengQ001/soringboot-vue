import apiClient from './axios'

/** 系统用户 CRUD */
export const sysUserApi = {
  list: () => apiClient.post('/system/users/list', {}),
  add: (data) => apiClient.post('/system/users/add', data),
  update: (data) => apiClient.post('/system/users/update', data),
  delete: (data) => apiClient.post('/system/users/delete', data)
}
