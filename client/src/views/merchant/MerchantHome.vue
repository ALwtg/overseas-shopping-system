<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">商家首页</h2>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
      <el-card shadow="hover">
        <div class="flex items-center">
          <div class="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center mr-4">
            <el-icon class="text-2xl text-blue-600"><ShoppingCart /></el-icon>
          </div>
          <div>
            <div class="text-gray-500 text-sm">今日订单</div>
            <div class="text-2xl font-bold">{{ stats.todayOrders }}</div>
          </div>
        </div>
      </el-card>
      <el-card shadow="hover">
        <div class="flex items-center">
          <div class="w-12 h-12 rounded-full bg-green-100 flex items-center justify-center mr-4">
            <el-icon class="text-2xl text-green-600"><Money /></el-icon>
          </div>
          <div>
            <div class="text-gray-500 text-sm">今日销售额</div>
            <div class="text-2xl font-bold">¥{{ stats.todaySales }}</div>
          </div>
        </div>
      </el-card>
      <el-card shadow="hover">
        <div class="flex items-center">
          <div class="w-12 h-12 rounded-full bg-orange-100 flex items-center justify-center mr-4">
            <el-icon class="text-2xl text-orange-600"><Goods /></el-icon>
          </div>
          <div>
            <div class="text-gray-500 text-sm">商品总数</div>
            <div class="text-2xl font-bold">{{ stats.totalProducts }}</div>
          </div>
        </div>
      </el-card>
      <el-card shadow="hover">
        <div class="flex items-center">
          <div class="w-12 h-12 rounded-full bg-purple-100 flex items-center justify-center mr-4">
            <el-icon class="text-2xl text-purple-600"><User /></el-icon>
          </div>
          <div>
            <div class="text-gray-500 text-sm">累计客户</div>
            <div class="text-2xl font-bold">{{ stats.totalCustomers }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <div class="grid lg:grid-cols-3 gap-6">
      <!-- 待办事项 -->
      <div class="lg:col-span-2">
        <el-card class="h-full">
          <template #header>
            <div class="flex items-center justify-between">
              <span class="font-bold">待办事项</span>
            </div>
          </template>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div class="text-center p-4 bg-yellow-50 rounded-lg cursor-pointer" @click="goToOrders('1')">
              <div class="text-3xl font-bold text-yellow-600">{{ todo.pendingShip }}</div>
              <div class="text-sm text-gray-600 mt-1">待发货</div>
            </div>
            <div class="text-center p-4 bg-blue-50 rounded-lg cursor-pointer" @click="goToOrders('2')">
              <div class="text-3xl font-bold text-blue-600">{{ todo.pendingReceive }}</div>
              <div class="text-sm text-gray-600 mt-1">待收货</div>
            </div>
            <div class="text-center p-4 bg-red-50 rounded-lg cursor-pointer" @click="goToReviews">
              <div class="text-3xl font-bold text-red-600">{{ todo.pendingReview }}</div>
              <div class="text-sm text-gray-600 mt-1">待回复评价</div>
            </div>
            <div class="text-center p-4 bg-gray-50 rounded-lg cursor-pointer" @click="goToProducts">
              <div class="text-3xl font-bold text-gray-600">{{ todo.lowStock }}</div>
              <div class="text-sm text-gray-600 mt-1">库存预警</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 快捷入口 -->
      <div>
        <el-card class="h-full">
          <template #header>
            <span class="font-bold">快捷入口</span>
          </template>
          <div class="grid grid-cols-2 gap-4">
            <el-button size="large" @click="$router.push('/merchant/products/add')">
              <el-icon class="mr-1"><Plus /></el-icon>
              发布商品
            </el-button>
            <el-button size="large" @click="$router.push('/merchant/orders')">
              <el-icon class="mr-1"><List /></el-icon>
              查看订单
            </el-button>
            <el-button size="large" @click="$router.push('/merchant/logistics')">
              <el-icon class="mr-1"><Van /></el-icon>
              批量发货
            </el-button>
            <el-button size="large" @click="$router.push('/merchant/statistics')">
              <el-icon class="mr-1"><TrendCharts /></el-icon>
              查看报表
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 销售趋势 -->
    <el-card class="mt-6">
      <template #header>
        <div class="flex items-center justify-between">
          <span class="font-bold">近7天销售趋势</span>
          <el-radio-group v-model="chartPeriod" size="small">
            <el-radio-button label="week">近7天</el-radio-button>
            <el-radio-button label="month">近30天</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="chartRef" style="height: 300px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ShoppingCart, Money, Goods, User, Plus, List, Van, TrendCharts } from '@element-plus/icons-vue'

const router = useRouter()
const chartRef = ref(null)
const chartPeriod = ref('week')

const stats = ref({
  todayOrders: 12,
  todaySales: 2580.50,
  totalProducts: 86,
  totalCustomers: 325
})

const todo = ref({
  pendingShip: 5,
  pendingReceive: 8,
  pendingReview: 3,
  lowStock: 2
})

const goToOrders = (status) => {
  router.push({ path: '/merchant/orders', query: { status } })
}

const goToReviews = () => {
  router.push('/merchant/reviews')
}

const goToProducts = () => {
  router.push({ path: '/merchant/products', query: { lowStock: '1' } })
}

const initChart = () => {
  // 这里使用简单的DOM操作模拟图表
  if (chartRef.value) {
    chartRef.value.innerHTML = `
      <div style="display: flex; align-items: flex-end; justify-content: space-around; height: 100%; padding: 20px;">
        ${[65, 80, 45, 90, 75, 85, 95].map((h, i) => `
          <div style="display: flex; flex-direction: column; align-items: center; flex: 1;">
            <div style="width: 40px; background: linear-gradient(to top, #3b82f6, #60a5fa); border-radius: 4px 4px 0 0; height: ${h * 2}px; transition: all 0.3s;"></div>
            <div style="margin-top: 8px; font-size: 12px; color: #666;">${['周一','周二','周三','周四','周五','周六','周日'][i]}</div>
          </div>
        `).join('')}
      </div>
    `
  }
}

onMounted(() => {
  nextTick(() => {
    initChart()
  })
})
</script>