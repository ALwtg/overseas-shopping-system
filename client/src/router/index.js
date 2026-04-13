import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/useUserStore'
import { getToken } from '@/utils/auth'

const routes = [
  { path: '/', component: () => import('@/views/frontend/Home.vue') },
  { path: '/login', name: 'Login', component: () => import('@/views/frontend/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/frontend/Register.vue') },
  { path: '/products', component: () => import('@/views/frontend/ProductList.vue') },
  { path: '/product/:id', component: () => import('@/views/frontend/ProductDetail.vue'), props: true },
  { path: '/cart', component: () => import('@/views/frontend/Cart.vue'), meta: { requiresAuth: true } },
  { path: '/order/confirm', component: () => import('@/views/frontend/OrderConfirm.vue'), meta: { requiresAuth: true } },
  { path: '/orders', component: () => import('@/views/frontend/OrderList.vue'), meta: { requiresAuth: true } },
  { path: '/order/:id', component: () => import('@/views/frontend/OrderDetail.vue'), meta: { requiresAuth: true }, props: true },
  { path: '/profile', component: () => import('@/views/frontend/UserProfile.vue'), meta: { requiresAuth: true } },
  { path: '/address', component: () => import('@/views/frontend/Address.vue'), meta: { requiresAuth: true } },
  { path: '/favorites', component: () => import('@/views/frontend/Favorite.vue'), meta: { requiresAuth: true } },

  // 商家后台
  {
    path: '/merchant',
    redirect: '/merchant/home',
    children: [
      { path: 'home', component: () => import('@/views/merchant/MerchantHome.vue'), meta: { requiresAuth: true, role: 1 } },
      { path: 'products', component: () => import('@/views/merchant/ProductManage.vue'), meta: { requiresAuth: true, role: 1 } },
      { path: 'orders', component: () => import('@/views/merchant/OrderManage.vue'), meta: { requiresAuth: true, role: 1 } },
      { path: 'finance', component: () => import('@/views/merchant/Statistics.vue'), meta: { requiresAuth: true, role: 1 } }
    ]
  },

  // 管理后台
  {
    path: '/admin',
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', component: () => import('@/views/admin/Dashboard.vue'), meta: { requiresAuth: true, role: 2 } },
      { path: 'users', component: () => import('@/views/admin/UserManage.vue'), meta: { requiresAuth: true, role: 2 } },
      { path: 'merchants', component: () => import('@/views/admin/MerchantManage.vue'), meta: { requiresAuth: true, role: 2 } },
      { path: 'products', component: () => import('@/views/admin/CategoryManage.vue'), meta: { requiresAuth: true, role: 2 } },
      { path: 'announcements', component: () => import('@/views/admin/Dashboard.vue'), meta: { requiresAuth: true, role: 2 } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() { return { top: 0 } }
})

router.beforeEach((to) => {
  const token = getToken()
  if (to.meta.requiresAuth && !token) return { name: 'Login' }
  if ((to.path.startsWith('/merchant') || to.path.startsWith('/admin')) && token) {
    const userStore = useUserStore()
    if (!userStore.userInfo) userStore.fetchUserInfo()
    if (to.meta.role === 1 && !userStore.isMerchant && !userStore.isAdmin) return '/'
    if (to.meta.role === 2 && !userStore.isAdmin) return '/'
  }
})

export default router
