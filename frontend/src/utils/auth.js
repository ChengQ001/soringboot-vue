/**
 * 登录态：与 localStorage 键名、退出逻辑集中管理，便于扩展（如 refreshToken、角色等）
 */

const STORAGE_KEYS = ['token', 'username', 'userId', 'phone', 'parkId', 'parks', 'roleIds', 'roles']

/** 清空本地登录信息（不跳转） */
export function clearAuthStorage() {
  STORAGE_KEYS.forEach((key) => localStorage.removeItem(key))
}

let navigateToLogin = null

/** 在 main.js 中注入，供 axios 401 等与路由解耦 */
export function setNavigateToLogin(fn) {
  navigateToLogin = typeof fn === 'function' ? fn : null
}

function goLogin() {
  if (navigateToLogin) {
    navigateToLogin()
  } else {
    window.location.replace(`${window.location.origin}/login`)
  }
}

/**
 * 仅清本地并回登录页（供 axios 401 等场景，避免再调退出接口造成循环）
 */
export function logoutLocal() {
  clearAuthStorage()
  goLogin()
}

/**
 * 退出登录：先请求后端 POST /auth/logout，再清本地并跳转登录页
 */
export async function logout() {
  try {
    if (isLoggedIn()) {
      const { authApi } = await import('../api/auth')
      await authApi.logout()
    }
  } catch (_) {
    // token 失效或网络错误仍执行本地退出
  } finally {
    clearAuthStorage()
    goLogin()
  }
}

export function isLoggedIn() {
  return Boolean(localStorage.getItem('token'))
}
