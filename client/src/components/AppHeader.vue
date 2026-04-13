<template>
  <header class="fixed top-0 left-0 right-0 z-50 bg-white/95 backdrop-blur-sm shadow-sm border-b border-blue-100">
    <!-- 顶部信息栏 -->
    <div class="bg-gradient-to-r from-primary-dark to-primary text-white text-xs py-1.5">
      <div class="max-w-7xl mx-auto px-4 flex justify-between items-center">
        <span>欢迎来到海外代购平台 - 全球好物一站购齐</span>
        <div class="flex gap-4">
          <span v-if="userStore.isLoggedIn" class="cursor-pointer hover:underline" @click="$router.push('/profile')">Hi, {{ userStore.userInfo?.nickname || '用户' }}</span>
          <template v-else>
            <span class="cursor-pointer hover:underline" @click="$router.push('/login')">登录</span>
            <span class="cursor-pointer hover:underline" @click="$router.push('/register')">注册</span>
          </template>
          <span v-if="userStore.isLoggedIn" class="cursor-pointer hover:underline text-orange-200" @click="handleLogout">退出</span>
        </div>
      </div>
    </div>

    <!-- 主导航栏 -->
    <div class="max-w-7xl mx-auto px-4 py-3 flex items-center gap-8">
      <h1 class="text-2xl font-bold cursor-pointer bg-gradient-to-r from-primary to-accent bg-clip-text text-transparent" @click="$router.push('/')">海外代购</h1>

      <nav class="flex items-center gap-6 text-sm font-medium">
        <a href="/" class="hover:text-primary transition-colors">首页</a>
        <a href="/products" class="hover:text-primary transition-colors">全部商品</a>
        <a href="/products?categoryId=1" class="hover:text-primary transition-colors">美妆护肤</a>
        <a href="/products?categoryId=2" class="hover:text-primary transition-colors">数码电子</a>
        <a href="/products?categoryId=3" class="hover:text-primary transition-colors">时尚服饰</a>
      </nav>

      <div class="flex-1 max-w-md">
        <div class="relative">
          <input
            v-model="keyword"
            type="text"
            placeholder="搜索海外商品..."
            class="w-full pl-10 pr-4 py-2 rounded-full border border-gray-200 focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition-all text-sm"
            @keyup.enter="handleSearch"
          />
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
        </div>
      </div>

      <div class="flex items-center gap-5">
        <button @click="$router.push('/favorites')" class="relative group">
          <Heart class="w-6 h-6 text-gray-600 group-hover:text-red-500 transition-colors" />
        </button>
        <button @click="$router.push('/cart')" class="relative group">
          <ShoppingCart class="w-6 h-6 text-gray-600 group-hover:text-primary transition-colors" />
          <span v-if="cartStore.totalItems > 0"
                class="absolute -top-2 -right-2 w-5 h-5 bg-accent text-white rounded-full text-xs flex items-center justify-center">
            {{ cartStore.totalItems }}
          </span>
        </button>

        <el-dropdown v-if="userStore.isMerchant || userStore.isAdmin" trigger="click">
          <button class="text-gray-600 hover:text-primary transition-colors">
            <Settings class="w-5 h-5" />
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="userStore.isMerchant" @click="$router.push('/merchant/home')">商家后台</el-dropdown-item>
              <el-dropdown-item v-if="userStore.isAdmin" @click="$router.push('/admin/dashboard')">管理后台</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Heart, ShoppingCart, Settings } from 'lucide-vue-next'
import { useUserStore } from '@/store/useUserStore'
import { useCartStore } from '@/store/useCartStore'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const keyword = ref('')

function handleSearch() {
  if (keyword.value.trim()) {
    router.push({ path: '/products', query: { keyword: keyword.value.trim() } })
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>
