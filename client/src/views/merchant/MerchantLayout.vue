<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 顶部导航 -->
    <header class="bg-white shadow-sm sticky top-0 z-50">
      <div class="flex items-center justify-between px-6 py-3">
        <div class="flex items-center gap-4">
          <h1 class="text-xl font-bold text-blue-600">商家后台</h1>
        </div>
        <div class="flex items-center gap-4">
          <el-button link @click="$router.push('/')">返回前台</el-button>
          <el-dropdown>
            <span class="flex items-center gap-2 cursor-pointer">
              <el-avatar :size="32" :src="merchantInfo.logo">
                {{ merchantInfo.name?.charAt(0) }}
              </el-avatar>
              <span>{{ merchantInfo.name }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/merchant/profile')">店铺信息</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <div class="flex">
      <!-- 侧边菜单 -->
      <aside class="w-64 bg-white shadow-md min-h-[calc(100vh-60px)]">
        <el-menu
          :default-active="$route.path"
          class="border-0"
          router
        >
          <el-menu-item index="/merchant">
            <el-icon><HomeFilled /></el-icon>
            <span>首页概览</span>
          </el-menu-item>
          <el-menu-item index="/merchant/products">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/merchant/orders">
            <el-icon><ShoppingCart /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/merchant/logistics">
            <el-icon><Van /></el-icon>
            <span>物流管理</span>
          </el-menu-item>
          <el-menu-item index="/merchant/reviews">
            <el-icon><ChatDotRound /></el-icon>
            <span>评价管理</span>
          </el-menu-item>
          <el-menu-item index="/merchant/statistics">
            <el-icon><TrendCharts /></el-icon>
            <span>数据统计</span>
          </el-menu-item>
          <el-menu-item index="/merchant/profile">
            <el-icon><Shop /></el-icon>
            <span>店铺信息</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- 主内容区 -->
      <main class="flex-1 p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled, Goods, ShoppingCart, Van, ChatDotRound, TrendCharts, Shop } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/useUserStore'

const router = useRouter()
const userStore = useUserStore()

const merchantInfo = ref({})

const initMerchantInfo = () => {
  merchantInfo.value = userStore.userInfo || {}
}

const logout = () => {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/login')
}

onMounted(initMerchantInfo)
</script>