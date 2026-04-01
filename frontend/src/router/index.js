import { createRouter, createWebHistory } from 'vue-router'
import { isLoggedIn } from '../utils/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/RegisterView.vue')
    },
    {
      path: '/home',
      name: 'Home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/admin',
      component: () => import('../layouts/AdminLayout.vue'),
      redirect: '/admin/users',
      children: [
        {
          path: 'users',
          name: 'AdminUsers',
          component: () => import('../views/admin/UserManage.vue')
        },
        {
          path: 'roles',
          name: 'AdminRoles',
          component: () => import('../views/admin/RoleManage.vue')
        },
        {
          path: 'menus',
          name: 'AdminMenus',
          component: () => import('../views/admin/MenuManage.vue')
        },
        {
          path: 'bind-role-menu',
          name: 'BindRoleMenu',
          component: () => import('../views/admin/BindRoleMenu.vue')
        },
        {
          path: 'bind-user-role',
          name: 'BindUserRole',
          component: () => import('../views/admin/BindUserRole.vue')
        },
        {
          path: 'parks',
          name: 'ParkManage',
          component: () => import('../views/admin/ParkManage.vue')
        },
        {
          path: 'no-access',
          name: 'AdminNoAccess',
          component: () => import('../views/admin/NoAccess.vue')
        }
      ]
    }
  ]
})

// 路由守卫，检查登录状态
router.beforeEach((to, from, next) => {
  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }

  if (!isLoggedIn()) {
    next('/login')
    return
  }

  next()
})

export default router
