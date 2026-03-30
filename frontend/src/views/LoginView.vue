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
          <input
            type="password"
            id="password"
            v-model="form.password"
            placeholder="请输入密码"
            required
          />
        </div>
        <button type="submit" :disabled="loading" class="login-btn">
          {{ loading ? '登录中...' : '登录' }}
        </button>
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
      const { token, username } = response.data
      const tokenValue = token.replace(/^Bearer\s+/i, '')
      localStorage.setItem('token', tokenValue)
      if (username) {
        localStorage.setItem('username', username)
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
</style>
