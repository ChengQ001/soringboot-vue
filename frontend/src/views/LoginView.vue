<template>
  <div class="login-container">
    <div class="login-box">
      <h2>用户登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="phone">手机号</label>
          <input
            type="tel"
            id="phone"
            v-model="form.phone"
            placeholder="请输入11位手机号"
            required
          />
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <div class="password-row">
            <input
              :type="passwordVisible ? 'text' : 'password'"
              id="password"
              v-model="form.password"
              placeholder="请输入密码"
              required
            />
            <button
              type="button"
              class="toggle-password"
              @click="passwordVisible = !passwordVisible"
            >
              {{ passwordVisible ? '隐藏' : '显示' }}
            </button>
          </div>
        </div>
        <button type="submit" :disabled="loading" class="login-btn">
          {{ loading ? '登录中...' : '登录' }}
        </button>
        <div class="default-accounts">
          <div class="default-title">开箱默认账号（初始化脚本仅创建此项）</div>
          <div class="default-row">
            <button type="button" class="pill" @click="fillDefault('admin')">
              admin / 17688888888 / 123456
            </button>
          </div>
        </div>
        <p class="footer-link">
          没有账号？
          <router-link to="/register">去注册</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api/auth'
import { flash } from '../utils/flash'

const router = useRouter()

const form = ref({
  phone: '',
  password: ''
})

const loading = ref(false)
const passwordVisible = ref(false)

function fillDefault(kind) {
  if (kind === 'admin') {
    form.value.phone = '17688888888'
    form.value.password = '123456'
  }
  passwordVisible.value = false
}

const handleLogin = async () => {
  const phone = (form.value.phone || '').trim()
  if (!/^1[3-9]\d{9}$/.test(phone)) {
    flash('请输入有效的11位手机号', 'error')
    return
  }

  loading.value = true

  try {
    // 后端接口字段：LoginRequest{ phone, password }
    // 这里 payload 直接与后端模型对齐，避免后续维护时字段语义漂移
    const payload = { phone, password: form.value.password }
    const response = await authApi.login(payload)
    if (response.code === 200) {
      const { token, username, defaultParkId, parks, id, phone, roleIds } = response.data
      const tokenValue = token.replace(/^Bearer\s+/i, '')
      localStorage.setItem('token', tokenValue)
      if (username) {
        localStorage.setItem('username', username)
      }
      if (id != null && id !== '') {
        localStorage.setItem('userId', String(id))
      } else {
        localStorage.removeItem('userId')
      }
      if (phone) {
        localStorage.setItem('phone', phone)
      } else {
        localStorage.removeItem('phone')
      }
      if (defaultParkId != null) {
        localStorage.setItem('parkId', String(defaultParkId))
      } else {
        localStorage.removeItem('parkId')
      }
      if (parks && Array.isArray(parks)) {
        localStorage.setItem('parks', JSON.stringify(parks))
      } else {
        localStorage.removeItem('parks')
      }
      if (roleIds && Array.isArray(roleIds) && roleIds.length) {
        localStorage.setItem('roleIds', JSON.stringify(roleIds))
      } else {
        localStorage.removeItem('roleIds')
      }
      router.push('/admin/menus')
    } else {
      flash(response.msg || '登录失败', 'error')
    }
  } catch (err) {
    flash(err.message || '登录失败，请检查网络连接', 'error')
    console.error('Login error:', err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-box h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1rem;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.password-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.password-row input {
  flex: 1;
}

.toggle-password {
  flex-shrink: 0;
  padding: 0.55rem 0.75rem;
  border-radius: 6px;
  border: 1px solid rgba(102, 126, 234, 0.25);
  background: rgba(102, 126, 234, 0.08);
  color: #1a56a8;
  cursor: pointer;
  font-size: 0.9rem;
  white-space: nowrap;
}

.toggle-password:hover {
  background: rgba(102, 126, 234, 0.14);
}

.login-btn {
  width: 100%;
  padding: 0.75rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  margin-top: 1rem;
}

.login-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.login-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.footer-link {
  margin-top: 1rem;
  text-align: center;
  font-size: 0.95rem;
  color: #666;
}

.footer-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.footer-link a:hover {
  text-decoration: underline;
}

.default-accounts {
  margin-top: 1rem;
  background: rgba(102, 126, 234, 0.06);
  border: 1px solid rgba(102, 126, 234, 0.18);
  border-radius: 10px;
  padding: 0.9rem;
}

.default-title {
  font-size: 0.85rem;
  color: #5b5f8f;
  margin-bottom: 0.5rem;
}

.default-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.pill {
  border: 1px solid rgba(102, 126, 234, 0.25);
  background: rgba(255, 255, 255, 0.75);
  color: #3d4a8f;
  padding: 0.45rem 0.65rem;
  border-radius: 999px;
  cursor: pointer;
  font-size: 0.85rem;
  white-space: nowrap;
}

.pill:hover {
  background: rgba(102, 126, 234, 0.12);
}
</style>
