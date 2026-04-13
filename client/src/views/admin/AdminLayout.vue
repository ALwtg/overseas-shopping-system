<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 顶部导航 -->
    <header class="bg-gray-800 text-white sticky top-0 z-50">
      <div class="flex items-center justify-between px-6 py-3">
        <div class="flex items-center gap-4">
          <h1 class="text-xl font-bold">管理后台</h1>
        </div>
        <div class="flex items-center gap-4">
          <el-button link class="text-white" @click="$router.push('/')">返回前台</el-button>
          <el-dropdown>
            <span class="flex items-center gap-2 cursor-pointer">
              <el-avatar :size="32" :src="adminInfo.avatar">
                {{ adminInfo.username?.charAt(0) }}
              </el-avatar>
              <span>{{ adminInfo.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
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
          <el-menu-item index="/admin">
            <el-icon><HomeFilled /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/merchants">
            <el-icon><Shop /></el-icon>
            <span>商家管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/products">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/orders">
            <el-icon><ShoppingCart /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/categories">
            <el-icon><Collection /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/announcements">
            <el-icon><Bell /></el-icon>
            <span>公告管理</span>
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
import { HomeFilled, User, Shop, Goods, ShoppingCart, Collection, Bell } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/useUserStore'

const router = useRouter()
const userStore = useUserStore()

const adminInfo = ref({})

const initAdminInfo = () => {
  adminInfo.value = userStore.userInfo || {}
}

const logout = () => {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/login')
}

onMounted(initAdminInfo)
</script>