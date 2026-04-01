<template>
  <div class="home">
    <header class="home-header">
      <h1>欢迎</h1>
      <p v-if="username" class="sub">已登录：{{ username }}</p>
      <button type="button" class="logout" :disabled="logoutLoading" @click="handleLogout">
        {{ logoutLoading ? '退出中…' : '退出登录' }}
      </button>
    </header>
    <main class="home-main">
      <p>你已登录。</p>
      <p class="actions">
        <router-link class="link-admin" to="/admin">进入系统管理</router-link>
      </p>
      <p class="hint">接口文档：<a href="http://localhost:8081/swagger" target="_blank" rel="noopener">Swagger</a></p>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { logout as doLogout } from '../utils/auth'

const username = ref('')
const logoutLoading = ref(false)

onMounted(() => {
  username.value = localStorage.getItem('username') || ''
})

async function handleLogout() {
  logoutLoading.value = true
  try {
    await doLogout()
  } finally {
    logoutLoading.value = false
  }
}
</script>

<style scoped>
.home {
  max-width: 720px;
  margin: 0 auto;
  padding: 2rem;
}

.home-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.home-header h1 {
  font-size: 1.75rem;
  color: #333;
}

.sub {
  color: #666;
  flex: 1;
  min-width: 200px;
}

.logout {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  background: #667eea;
  color: #fff;
  cursor: pointer;
  font-size: 0.95rem;
}

.logout:hover {
  opacity: 0.92;
}

.home-main {
  background: #fff;
  border-radius: 10px;
  padding: 1.5rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.hint {
  margin-top: 1rem;
  font-size: 0.9rem;
}

.hint a {
  color: #667eea;
}

.actions {
  margin: 1rem 0;
}

.link-admin {
  display: inline-block;
  padding: 0.5rem 1.25rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff !important;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 500;
}

.link-admin:hover {
  opacity: 0.92;
}
</style>
