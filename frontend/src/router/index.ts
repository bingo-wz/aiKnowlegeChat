import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/classroom'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/classroom',
    name: 'ClassroomList',
    component: () => import('@/views/classroom/ClassroomList.vue'),
    meta: { title: '课堂列表', requiresAuth: true }
  },
  {
    path: '/classroom/:id',
    name: 'ClassroomDetail',
    component: () => import('@/views/classroom/ClassroomDetail.vue'),
    meta: { title: '课堂详情', requiresAuth: true }
  },
  {
    path: '/research',
    name: 'Research',
    component: () => import('@/views/research/ResearchRoom.vue'),
    meta: { title: '教研室', requiresAuth: true }
  },
  {
    path: '/knowledge',
    name: 'Knowledge',
    component: () => import('@/views/knowledge/KnowledgeBase.vue'),
    meta: { title: '知识库管理', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.meta.requiresAuth

  if (requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/classroom')
  } else {
    next()
  }
})

export default router
