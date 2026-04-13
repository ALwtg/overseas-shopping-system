<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">数据概览</h2>

    <!-- 核心指标 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-blue-600">{{ stats.totalUsers }}</div>
        <div class="text-sm text-gray-500 mt-1">注册用户</div>
        <div class="text-green-500 text-sm mt-2">↑ {{ stats.userGrowth }}%</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-green-600">{{ stats.totalMerchants }}</div>
        <div class="text-sm text-gray-500 mt-1">入驻商家</div>
        <div class="text-green-500 text-sm mt-2">↑ {{ stats.merchantGrowth }}%</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-orange-600">{{ stats.totalOrders }}</div>
        <div class="text-sm text-gray-500 mt-1">今日订单</div>
        <div class="text-green-500 text-sm mt-2">↑ {{ stats.orderGrowth }}%</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-red-600">¥{{ stats.todaySales }}</div>
        <div class="text-sm text-gray-500 mt-1">今日销售额</div>
        <div class="text-green-500 text-sm mt-2">↑ {{ stats.salesGrowth }}%</div>
      </el-card>
    </div>

    <div class="grid lg:grid-cols-2 gap-6 mb-6">
      <!-- 销售趋势 -->
      <el-card>
        <template #header>
          <div class="flex items-center justify-between">
            <span class="font-bold">近7天销售趋势</span>
            <el-radio-group v-model="chartPeriod" size="small">
              <el-radio-button label="week">近7天</el-radio-button>
              <el-radio-button label="month">近30天</el-radio-button>
            </el-radio-group>
          </div>
        </template>
        <div ref="salesChart" style="height: 300px;"></div>
      </el-card>

      <!-- 用户增长 -->
      <el-card>
        <template #header>
          <span class="font-bold">用户增长趋势</span>
        </template>
        <div ref="userChart" style="height: 300px;"></div>
      </el-card>
    </div>

    <div class="grid lg:grid-cols-3 gap-6">
      <!-- 订单状态分布 -->
      <el-card>
        <template #header>
          <span class="font-bold">订单状态分布</span>
        </template>
        <div ref="orderChart" style="height: 250px;"></div>
      </el-card>

      <!-- 商品分类销售 -->
      <el-card>
        <template #header>
          <span class="font-bold">商品分类销售占比</span>
        </template>
        <div ref="categoryChart" style="height: 250px;"></div>
      </el-card>

      <!-- 待处理事项 -->
      <el-card>
        <template #header>
          <span class="font-bold">待处理事项</span>
        </template>
        <div class="space-y-4">
          <div class="flex items-center justify-between p-3 bg-yellow-50 rounded-lg cursor-pointer" @click="goTo('/admin/merchants')">
            <div class="flex items-center gap-3">
              <el-icon class="text-yellow-600 text-xl"><Warning /></el-icon>
              <span>待审核商家</span>
            </div>
            <el-tag type="warning">{{ pending.merchant }}</el-tag>
          </div>
          <div class="flex items-center justify-between p-3 bg-red-50 rounded-lg cursor-pointer" @click="goTo('/admin/products')">
            <div class="flex items-center gap-3">
              <el-icon class="text-red-600 text-xl"><CircleCheck /></el-icon>
              <span>待审核商品</span>
            </div>
            <el-tag type="danger">{{ pending.product }}</el-tag>
          </div>
          <div class="flex items-center justify-between p-3 bg-blue-50 rounded-lg cursor-pointer" @click="goTo('/admin/orders')">
            <div class="flex items-center gap-3">
              <el-icon class="text-blue-600 text-xl"><QuestionFilled /></el-icon>
              <span>待处理售后</span>
            </div>
            <el-tag type="primary">{{ pending.afterSale }}</el-tag>
          </div>
          <div class="flex items-center justify-between p-3 bg-orange-50 rounded-lg cursor-pointer" @click="goTo('/admin/users')">
            <div class="flex items-center gap-3">
              <el-icon class="text-orange-600 text-xl"><User /></el-icon>
              <span>待审核认证</span>
            </div>
            <el-tag type="warning">{{ pending.verify }}</el-tag>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Warning, CircleCheck, QuestionFilled, User } from '@element-plus/icons-vue'

const router = useRouter()

const stats = ref({
  totalUsers: 3256,
  userGrowth: 12.5,
  totalMerchants: 128,
  merchantGrowth: 8.3,
  totalOrders: 568,
  orderGrowth: 15.2,
  todaySales: '68,520.00',
  salesGrowth: 18.6
})

const pending = ref({
  merchant: 5,
  product: 12,
  afterSale: 8,
  verify: 3
})

const chartPeriod = ref('week')
const salesChart = ref(null)
const userChart = ref(null)
const orderChart = ref(null)
const categoryChart = ref(null)

const goTo = (path) => {
  router.push(path)
}

const initCharts = () => {
  // 销售趋势图
  if (salesChart.value) {
    salesChart.value.innerHTML = `
      <div style="display: flex; align-items: flex-end; justify-content: space-around; height: 100%; padding: 20px;">
        ${[4500, 5200, 4800, 6100, 5800, 7200, 6850].map((v, i) => `
          <div style="display: flex; flex-direction: column; align-items: center; flex: 1;">
            <div style="width: 30px; background: linear-gradient(to top, #3b82f6, #60a5fa); border-radius: 4px 4px 0 0; height: ${v / 30}px;"></div>
            <div style="margin-top: 8px; font-size: 12px; color: #666;">${['1/9', '1/10', '1/11', '1/12', '1/13', '1/14', '1/15'][i]}</div>
          </div>
        `).join('')}
      </div>
    `
  }

  // 用户增长图
  if (userChart.value) {
    userChart.value.innerHTML = `
      <div style="display: flex; align-items: center; height: 100%; padding: 20px;">
        <div style="flex: 1; height: 2px; background: linear-gradient(to right, #10b981, #34d399); position: relative;">
          ${[20, 35, 45, 60, 75, 90, 100].map((v, i) => `
            <div style="position: absolute; left: ${i * 16}%; bottom: 0; width: 10px; height: 10px; background: #10b981; border-radius: 50%; transform: translateY(50%);"></div>
          `).join('')}
        </div>
      </div>
      <div style="display: flex; justify-content: space-between; padding: 0 20px; font-size: 12px; color: #666;">
        <span>1/9</span><span>1/10</span><span>1/11</span><span>1/12</span><span>1/13</span><span>1/14</span><span>1/15</span>
      </div>
    `
  }

  // 订单状态分布
  if (orderChart.value) {
    orderChart.value.innerHTML = `
      <div style="display: flex; align-items: center; justify-content: center; height: 100%;">
        <div style="width: 150px; height: 150px; border-radius: 50%; background: conic-gradient(#67c23a 0% 55%, #e6a23c 55% 80%, #f56c6c 80% 95%, #909399 95% 100%);"></div>
        <div style="margin-left: 30px; font-size: 12px;">
          <div style="margin-bottom: 8px;"><span style="display: inline-block; width: 10px; height: 10px; background: #67c23a; border-radius: 50%; margin-right: 8px;"></span>已完成 55%</div>
          <div style="margin-bottom: 8px;"><span style="display: inline-block; width: 10px; height: 10px; background: #e6a23c; border-radius: 50%; margin-right: 8px;"></span>进行中 25%</div>
          <div style="margin-bottom: 8px;"><span style="display: inline-block; width: 10px; height: 10px; background: #f56c6c; border-radius: 50%; margin-right: 8px;"></span>待处理 15%</div>
          <div><span style="display: inline-block; width: 10px; height: 10px; background: #909399; border-radius: 50%; margin-right: 8px;"></span>已取消 5%</div>
        </div>
      </div>
    `
  }

  // 分类销售占比
  if (categoryChart.value) {
    categoryChart.value.innerHTML = `
      <div style="display: flex; flex-direction: column; justify-content: center; height: 100%; gap: 12px; padding: 20px;">
        <div style="display: flex; align-items: center; gap: 10px;">
          <div style="width: 80px; font-size: 12px; color: #666;">美妆护肤</div>
          <div style="flex: 1; height: 20px; background: #f0f0f0; border-radius: 10px; overflow: hidden;">
            <div style="width: 35%; height: 100%; background: linear-gradient(to right, #3b82f6, #60a5fa); border-radius: 10px;"></div>
          </div>
          <div style="width: 40px; font-size: 12px; color: #333;">35%</div>
        </div>
        <div style="display: flex; align-items: center; gap: 10px;">
          <div style="width: 80px; font-size: 12px; color: #666;">母婴用品</div>
          <div style="flex: 1; height: 20px; background: #f0f0f0; border-radius: 10px; overflow: hidden;">
            <div style="width: 28%; height: 100%; background: linear-gradient(to right, #10b981, #34d399); border-radius: 10px;"></div>
          </div>
          <div style="width: 40px; font-size: 12px; color: #333;">28%</div>
        </div>
        <div style="display: flex; align-items: center; gap: 10px;">
          <div style="width: 80px; font-size: 12px; color: #666;">保健品</div>
          <div style="flex: 1; height: 20px; background: #f0f0f0; border-radius: 10px; overflow: hidden;">
            <div style="width: 22%; height: 100%; background: linear-gradient(to right, #f59e0b, #fbbf24); border-radius: 10px;"></div>
          </div>
          <div style="width: 40px; font-size: 12px; color: #333;">22%</div>
        </div>
        <div style="display: flex; align-items: center; gap: 10px;">
          <div style="width: 80px; font-size: 12px; color: #666;">其他</div>
          <div style="flex: 1; height: 20px; background: #f0f0f0; border-radius: 10px; overflow: hidden;">
            <div style="width: 15%; height: 100%; background: linear-gradient(to right, #8b5cf6, #a78bfa); border-radius: 10px;"></div>
          </div>
          <div style="width: 40px; font-size: 12px; color: #333;">15%</div>
        </div>
      </div>
    `
  }
}

onMounted(() => {
  nextTick(() => {
    initCharts()
  })
})
</script>