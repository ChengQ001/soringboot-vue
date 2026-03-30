<template>
  <div class="login-container">
    <div class="login-box">
      <h2>用户注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="reg-username">用户名</label>
          <input
            id="reg-username"
            v-model="form.username"
            type="text"
            autocomplete="username"
            placeholder="登录使用的用户名"
            required
          />
        </div>
        <div class="form-group">
          <label for="reg-phone">手机号</label>
          <input
            id="reg-phone"
            v-model="form.phone"
            type="tel"
            maxlength="11"
            autocomplete="tel"
            placeholder="11位手机号，用于唯一校验"
            required
          />
        </div>
        <div class="form-group">
          <label for="reg-password">密码</label>
          <input
            id="reg-password"
            v-model="form.password"
            type="password"
            autocomplete="new-password"
            placeholder="请输入密码"
            required
          />
        </div>
        <div class="form-group">
          <label for="reg-confirm">确认密码</label>
          <input
            id="reg-confirm"
            v-model="confirmPassword"
            type="password"
            autocomplete="new-password"
            placeholder="请再次输入密码"
            required
          />
        </div>
        <div class="form-group">
          <label for="reg-desc">用户描述 <span class="optional">（选填）</span></label>
          <textarea
            id="reg-desc"
            v-model="form.description"
            rows="3"
            placeholder="可填写备注说明"
          />
        </div>
        <button type="submit" :disabled="loading" class="login-btn">
          {{ loading ? '注册中...' : '注册' }}
        </button>
        <p class="footer-link">
          已有账号？
          <router-link to="/login">去登录</router-link>
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
  username: '',
  phone: '',
  password: '',
  description: ''
})

const confirmPassword = ref('')
const loading = ref(false)

const handleRegister = async () => {
  if (form.value.password !== confirmPassword.value) {
    flash('两次输入的密码不一致', 'error')
    return
  }
  const phone = (form.value.phone || '').trim()
  if (!/^1[3-9]\d{9}$/.test(phone)) {
    flash('请输入有效的11位手机号', 'error')
    return
  }
  loading.value = true
  try {
    const payload = {
      username: form.value.username.trim(),
      phone,
      password: form.value.password,
      description: (form.value.description || '').trim() || undefined
    }
    const response = await authApi.register(payload)
    if (response.code === 200) {
      flash(response.msg || '注册成功，正在跳转登录…', 'success', 2000)
      setTimeout(() => router.push('/login'), 1600)
    } else {
      flash(response.msg || '注册失败', 'error')
    }
  } catch (err) {
    flash(err.message || '注册失败，请检查网络连接', 'error')
    console.error('Register error:', err)
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

.optional {
  font-weight: 400;
  color: #999;
  font-size: 0.85rem;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1rem;
  box-sizing: border-box;
  background: #fff;
  font-family: inherit;
}

.form-group textarea {
  resize: vertical;
  min-height: 4.5rem;
}

.form-group input:focus,
.form-group textarea:focus {
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
