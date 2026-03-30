import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { setNavigateToLogin } from './utils/auth'

setNavigateToLogin(() => router.replace('/login'))

createApp(App).use(router).mount('#app')
