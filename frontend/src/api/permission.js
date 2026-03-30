import apiClient from './axios'

/** 权限绑定：后端为 POST */
export const permissionApi = {
  bindRoleMenus: (data) => apiClient.post('/permissions/role-menus', data),
  bindUserRoles: (data) => apiClient.post('/permissions/user-roles', data)
}
