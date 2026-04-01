<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="brand">系统管理</div>
      <nav class="nav">
        <template v-if="menuRoots && menuRoots.length">
          <template v-for="root in menuRoots" :key="root.id">
            <div class="nav-group">{{ root.name }}</div>
            <router-link
              v-for="child in (root.children || [])"
              :key="child.id"
              class="nav-item"
              :to="child.path"
            >
              {{ child.name }}
            </router-link>
          </template>
        </template>
        <template v-else>
          <div class="nav-group">暂无菜单</div>
        </template>
      </nav>
      <div class="sidebar-footer">
        <router-link to="/home" class="link-home">返回首页</router-link>
      </div>
    </aside>
    <main class="main">
      <div class="main-topbar">
        <div v-if="parkOptions.length" class="park-switch">
          <label for="park-select">园区</label>
          <select id="park-select" v-model="selectedParkId" class="park-select" @change="onParkChange">
            <option v-for="p in parkOptions" :key="p.id" :value="String(p.id)">{{ p.name }}</option>
          </select>
        </div>
        <div class="user-widget" ref="userWidgetRef">
          <!-- 用户下拉菜单：个人信息/修改密码/退出登录 -->
          <div class="user-trigger" @click="toggleUserDropdown">
            <span class="user-label">用户：</span>
            <span class="user-name">{{ username }}</span>
            <span class="caret">▼</span>
          </div>

          <!-- 下拉选项 -->
          <div v-if="userDropdownVisible" class="user-dropdown">
            <button type="button" class="dd-item" @click="openUserModal">个人信息</button>
            <button type="button" class="dd-item" @click="openChangePasswordModal">修改密码</button>
            <button type="button" class="dd-item danger" @click="handleLogout">退出登录</button>
          </div>
        </div>
      </div>

      <router-view />

      <!-- 个人信息弹窗 -->
      <div v-if="userModalVisible" class="modal-mask">
        <div class="modal">
          <h3>个人信息</h3>

          <div v-if="userDetailLoading" class="modal-body">加载中…</div>
          <div v-else class="modal-body">
            <div class="kv">
              <span class="k">用户名</span>
              <span class="v">{{ userDetail?.username || username }}</span>
            </div>
            <div class="kv" v-if="userDetail?.phone">
              <span class="k">手机号</span>
              <span class="v">{{ userDetail.phone }}</span>
            </div>
            <div class="kv">
              <span class="k">角色</span>
              <span class="v">{{ rolesText }}</span>
            </div>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn-secondary" @click="closeUserModal">关闭</button>
          </div>
        </div>
      </div>

      <!-- 修改密码弹窗 -->
      <div v-if="changePasswordModalVisible" class="modal-mask">
        <div class="modal">
          <h3>修改密码</h3>

          <div class="modal-body">
            <div class="form-row">
              <label>新密码</label>
              <input v-model="newPassword" type="password" placeholder="请输入新密码" />
            </div>
            <div class="form-row">
              <label>确认密码</label>
              <input v-model="confirmPassword" type="password" placeholder="请再次输入新密码" />
            </div>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn-secondary" :disabled="passwordSaving" @click="closeChangePasswordModal">
              取消
            </button>
            <button type="button" class="btn" :disabled="passwordSaving" @click="submitChangePassword">
              {{ passwordSaving ? '保存中…' : '保存' }}
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi } from '../api/user'
import { authApi } from '../api/auth'
import { menuApi } from '../api/menu'
import { logout as doLogout } from '../utils/auth'
import { flash } from '../utils/flash'

const router = useRouter()
const route = useRoute()

const username = ref(localStorage.getItem('username') || '')

const menuRoots = ref([])

const parkOptions = ref([])
const selectedParkId = ref(localStorage.getItem('parkId') || '')

function loadParkOptions() {
  try {
    const raw = localStorage.getItem('parks')
    parkOptions.value = raw ? JSON.parse(raw) : []
    if (!selectedParkId.value && parkOptions.value.length) {
      selectedParkId.value = String(parkOptions.value[0].id)
      localStorage.setItem('parkId', selectedParkId.value)
    }
  } catch {
    parkOptions.value = []
  }
}

async function onParkChange() {
  const v = selectedParkId.value
  if (v) {
    localStorage.setItem('parkId', v)
  } else {
    localStorage.removeItem('parkId')
  }
  await fetchMenuTree()
}

const userWidgetRef = ref(null)
const userDropdownVisible = ref(false)

const userModalVisible = ref(false)
const changePasswordModalVisible = ref(false)

const userDetailLoading = ref(false)
const userDetail = ref(null)

const newPassword = ref('')
const confirmPassword = ref('')
const passwordSaving = ref(false)

function closeDropdown() {
  userDropdownVisible.value = false
}

function toggleUserDropdown() {
  userDropdownVisible.value = !userDropdownVisible.value
}

function closeUserModal() {
  userModalVisible.value = false
}

function closeChangePasswordModal() {
  changePasswordModalVisible.value = false
  newPassword.value = ''
  confirmPassword.value = ''
}

function normalizeAuthority(a) {
  if (!a) return ''
  if (typeof a === 'string') return a
  if (typeof a === 'object') return a.authority || a.name || ''
  return String(a)
}

const rolesText = computed(() => {
  const auth = userDetail.value?.authorities || []
  if (!Array.isArray(auth) || auth.length === 0) return '无角色'
  const roleNames = auth
    .map((a) => normalizeAuthority(a))
    .filter(Boolean)
    .filter((s) => s.startsWith('ROLE_'))
    .map((s) => s.replace(/^ROLE_/, ''))
  return roleNames.length ? roleNames.join(', ') : '无角色'
})

async function fetchUserDetail() {
  userDetailLoading.value = true
  try {
    // 后端：POST /users/current-user
    // 返回值为 Spring Security 当前登录 principal（User 实体，包含 id/phone/username + roles/permissions）
    const res = await userApi.getCurrentUser({})
    if (res.code === 200) {
      userDetail.value = res.data || null
    } else {
      flash(res.msg || '获取用户信息失败', 'error')
    }
  } catch (e) {
    flash(e?.message || '获取用户信息失败', 'error')
  } finally {
    userDetailLoading.value = false
  }
}

function collectMenuPaths(roots) {
  const paths = []
  for (const r of roots || []) {
    for (const c of r.children || []) {
      if (c.path) paths.push(c.path)
    }
  }
  return paths
}

/** 切换园区或首次加载后：无菜单则进无权限页；当前页不在可见菜单里则跳到第一个可见页 */
function redirectByMenuAccess() {
  const paths = collectMenuPaths(menuRoots.value)
  const cur = route.path
  if (paths.length === 0) {
    if (cur !== '/admin/no-access') {
      router.replace('/admin/no-access')
    }
    return
  }
  if (cur === '/admin/no-access' || !paths.includes(cur)) {
    router.replace(paths[0])
  }
}

async function fetchMenuTree() {
  try {
    const res = await menuApi.tree({})
    if (res.code === 200) {
      menuRoots.value = res.data || []
      redirectByMenuAccess()
    } else {
      flash(res.msg || '获取菜单失败', 'error')
    }
  } catch (e) {
    flash(e?.message || '获取菜单失败', 'error')
  }
}

async function openUserModal() {
  closeDropdown()
  userModalVisible.value = true
  await fetchUserDetail()
}

async function openChangePasswordModal() {
  closeDropdown()
  changePasswordModalVisible.value = true
  newPassword.value = ''
  confirmPassword.value = ''
  await fetchUserDetail()
}

async function handleLogout() {
  closeDropdown()
  try {
    await doLogout()
  } catch (_) {
    // logout() 内部已做兜底清理，这里仅忽略异常
  }
}

async function submitChangePassword() {
  if (passwordSaving.value) return

  const np = (newPassword.value || '').trim()
  const cp = (confirmPassword.value || '').trim()
  if (!np) {
    flash('请输入新密码', 'error')
    return
  }
  if (np !== cp) {
    flash('两次输入的密码不一致', 'error')
    return
  }

  const id = userDetail.value?.id
  if (!id) {
    flash('无法获取用户ID，请刷新后重试', 'error')
    return
  }

  passwordSaving.value = true
  try {
    // 后端：POST /auth/update
    // UpdateUserRequest{ id, password }，修改成功后退出登录回到 /login
    const res = await authApi.updateUser({ id, password: np })
    if (res.code === 200) {
      flash(res.msg ? `${res.msg}` : '修改密码成功', 'success')
      closeChangePasswordModal()
      // 密码修改后退出登录，避免 token 与密码状态不一致
      await doLogout()
      return
    } else {
      flash(res.msg || '修改密码失败', 'error')
    }
  } catch (e) {
    flash(e?.message || '修改密码失败', 'error')
  } finally {
    passwordSaving.value = false
  }
}

function onGlobalClick(e) {
  if (!userWidgetRef.value) return
  const target = e.target
  if (target && !userWidgetRef.value.contains(target)) closeDropdown()
}

onMounted(() => {
  document.addEventListener('click', onGlobalClick)
  loadParkOptions()
  fetchMenuTree()
})

onBeforeUnmount(() => {
  document.removeEventListener('click', onGlobalClick)
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #3d4a8f 0%, #2c335c 100%);
  color: #e8eaf6;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.brand {
  padding: 1.25rem 1rem;
  font-weight: 600;
  font-size: 1.05rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.nav {
  flex: 1;
  padding: 0.75rem 0;
  overflow-y: auto;
}

.nav-group {
  padding: 0.65rem 1rem 0.35rem;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  opacity: 0.55;
}

.nav-item {
  display: block;
  padding: 0.55rem 1rem;
  margin: 0 0.5rem 0.2rem;
  border-radius: 6px;
  color: #c5cae9;
  text-decoration: none;
  font-size: 0.92rem;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
}

.nav-item.router-link-active {
  background: rgba(102, 126, 234, 0.35);
  color: #fff;
}

.sidebar-footer {
  padding: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.link-home {
  color: #9fa8da;
  font-size: 0.88rem;
  text-decoration: none;
}

.link-home:hover {
  color: #fff;
}

.main {
  flex: 1;
  padding: 1.25rem 1.5rem;
  overflow: auto;
}

.main-topbar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.park-switch {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-right: auto;
  font-size: 0.9rem;
  color: #555;
}

.park-select {
  padding: 0.35rem 0.6rem;
  border-radius: 6px;
  border: 1px solid #ddd;
  background: #fff;
  min-width: 140px;
  font-size: 0.9rem;
}

.user-widget {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  position: relative;
}

.user-label {
  color: #666;
  font-size: 0.9rem;
}

.user-name {
  font-weight: 600;
  color: #333;
}

.user-btn {
  padding: 0.35rem 0.85rem;
  border-radius: 6px;
  border: 1px solid rgba(102, 126, 234, 0.25);
  background: rgba(102, 126, 234, 0.08);
  color: #1a56a8;
  cursor: pointer;
  font-size: 0.9rem;
}

.user-btn:hover:not(:disabled) {
  background: rgba(102, 126, 234, 0.14);
}

.user-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  cursor: pointer;
  user-select: none;
}

.caret {
  font-size: 0.75rem;
  opacity: 0.6;
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
  border: 1px solid #eaeef6;
  padding: 0.25rem;
  min-width: 180px;
  z-index: 10000;
}

.dd-item {
  width: 100%;
  text-align: left;
  background: transparent;
  border: none;
  padding: 0.55rem 0.75rem;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  color: #333;
}

.dd-item:hover {
  background: #f3f6ff;
}

.dd-item.danger {
  color: #c0392b;
}

.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.modal {
  background: #fff;
  padding: 1.25rem;
  border-radius: 10px;
  width: 100%;
  max-width: 520px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.modal-body {
  margin-top: 0.75rem;
}

.kv {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.25rem 0;
}

.k {
  color: #666;
}

.v {
  color: #333;
  text-align: right;
  word-break: break-word;
}

.raw pre {
  background: #f6f7f9;
  padding: 0.75rem;
  border-radius: 6px;
  max-height: 50vh;
  overflow: auto;
}

.modal-actions {
  margin-top: 1.25rem;
  display: flex;
  justify-content: flex-end;
}

.btn {
  padding: 0.5rem 1.1rem;
  border: none;
  border-radius: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  cursor: pointer;
}

.btn:hover {
  opacity: 0.95;
}

.btn-secondary {
  padding: 0.5rem 1.1rem;
  border-radius: 6px;
  background: #fff;
  border: 1px solid #ddd;
  color: #333;
  cursor: pointer;
}

.btn-secondary:hover {
  opacity: 0.95;
}

.form-row {
  margin-top: 0.9rem;
}

.form-row label {
  display: block;
  font-size: 0.85rem;
  color: #555;
  margin-bottom: 0.25rem;
}

.form-row input {
  width: 100%;
  padding: 0.45rem 0.5rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-sizing: border-box;
  font-family: inherit;
}
</style>
