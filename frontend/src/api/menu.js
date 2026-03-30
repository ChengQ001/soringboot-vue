import apiClient from './axios'

/** 菜单：后端全部为 POST */
export const menuApi = {
  create: (data) => apiClient.post('/menus', data),
  update: (data) => apiClient.post('/menus/update', data),
  remove: (data) => apiClient.post('/menus/delete', data),
  detail: (data) => apiClient.post('/menus/detail', data),
  list: (data = {}) => apiClient.post('/menus/list', data),
  tree: (data = {}) => apiClient.post('/menus/tree', data),
  root: (data = {}) => apiClient.post('/menus/root', data),
  children: (data) => apiClient.post('/menus/children', data)
}
