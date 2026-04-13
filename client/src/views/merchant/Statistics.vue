<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">数据统计</h2>

    <!-- 数据概览 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-blue-600">¥{{ overview.totalSales }}</div>
        <div class="text-sm text-gray-500 mt-1">累计销售额</div>
        <div class="text-green-500 text-sm mt-2">↑ 12.5%</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-green-600">{{ overview.totalOrders }}</div>
        <div class="text-sm text-gray-500 mt-1">累计订单</div>
        <div class="text-green-500 text-sm mt-2">↑ 8.3%</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-orange-600">{{ overview.totalProducts }}</div>
        <div class="text-sm text-gray-500 mt-1">商品数量</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-purple-600">{{ overview.totalCustomers }}</div>
        <div class="text-sm text-gray-500 mt-1">累计客户</div>
        <div class="text-green-500 text-sm mt-2">↑ 15.2%</div>
      </el-card>
    </div>

    <div class="grid lg:grid-cols-2 gap-6 mb-6">
      <!-- 销售趋势 -->
      <el-card>
        <template #header>
          <div class="flex items-center justify-between">
            <span class="font-bold">销售趋势</span>
            <el-radio-group v-model="salesPeriod" size="small">
              <el-radio-button label="week">近7天</el-radio-button>
              <el-radio-button label="month">近30天</el-radio-button>
            </el-radio-group>
          </div>
        </template>
        <div ref="salesChart" style="height: 300px;"></div>
      </el-card>

      <!-- 订单状态分布 -->
      <el-card>
        <template #header>
          <span class="font-bold">订单状态分布</span>
        </template>
        <div ref="orderChart" style="height: 300px;"></div>
      </el-card>
    </div>

    <div class="grid lg:grid-cols-2 gap-6">
      <!-- 热销商品排行 -->
      <el-card>
        <template #header>
          <span class="font-bold">热销商品 TOP10</span>
        </template>
        <el-table :data="hotProducts" stripe>
          <el-table-column type="index" width="50" />
          <el-table-column label="商品名称" prop="name" />
          <el-table-column label="销量" prop="sold" width="100" />
          <el-table-column label="销售额" width="120">
            <template #default="{ row }">
              <span class="text-red-600">¥{{ row.sales }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 销售分类占比 -->
      <el-card>
        <template #header>
          <span class="font-bold">销售分类占比</span>
        </template>
        <div ref="categoryChart" style="height: 300px;"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'

const overview = ref({
  totalSales: '125,680.50',
  totalOrders: 856,
  totalProducts: 86,
  totalCustomers: 325
})

const salesPeriod = ref('week')
const salesChart = ref(null)
const orderChart = ref(null)
const categoryChart = ref(null)

const hotProducts = ref([
  { name: 'SK-II神仙水 230ml', sold: 156, sales: '202,644' },
  { name: '爱他美奶粉 3段', sold: 89, sales: '23,852' },
  { name: '兰蔻小黑瓶精华', sold: 67, sales: '53,733' },
  { name: '雅诗兰黛小棕瓶', sold: 54, sales: '45,846' },
  { name: 'Swisse维生素C', sold: 128, sales: '19,200' }
])

const initCharts = () => {
  // 销售趋势图（模拟）
  if (salesChart.value) {
    salesChart.value.innerHTML = `
      <div style="display: flex; align-items: flex-end; justify-content: space-around; height: 100%; padding: 20px;">
        ${[3200, 4500, 3800, 5200, 4800, 6100, 5800].map((v, i) => `
          <div style="display: flex; flex-direction: column; align-items: center; flex: 1;">
            <div style="width: 30px; background: linear-gradient(to top, #3b82f6, #60a5fa); border-radius: 4px 4px 0 0; height: ${v / 30}px;"></div>
            <div style="margin-top: 8px; font-size: 12px; color: #666;">${['1/9', '1/10', '1/11', '1/12', '1/13', '1/14', '1/15'][i]}</div>
          </div>
        `).join('')}
      </div>
    `
  }

  // 订单状态分布（模拟）
  if (orderChart.value) {
    orderChart.value.innerHTML = `
      <div style="display: flex; align-items: center; justify-content: center; height: 100%;">
        <div style="display: flex; gap: 40px;">
          <div style="text-align: center;">
            <div style="width: 100px; height: 100px; border-radius: 50%; background: conic-gradient(#67c23a 0% 60%, #e6a23c 60% 80%, #f56c6c 80% 95%, #909399 95% 100%);"></div>
            <div style="margin-top: 16px; display: flex; gap: 16px; font-size: 12px;">
              <span><span style="display: inline-block; width: 10px; height: 10px; background: #67c23a; border-radius: 50%; margin-right: 4px;"></span>已完成 60%</span>
              <span><span style="display: inline-block; width: 10px; height: 10px; background: #e6a23c; border-radius: 50%; margin-right: 4px;"></span>进行中 20%</span>
              <span><span style="display: inline-block; width: 10px; height: 10px; background: #f56c6c; border-radius: 50%; margin-right: 4px;"></span>待处理 15%</span>
            </div>
          </div>
        </div>
      </div>
    `
  }

  // 分类占比（模拟）
  if (categoryChart.value) {
    categoryChart.value.innerHTML = `
      <div style="display: flex; align-items: center; justify-content: center; height: 100%;">
        <div style="display: flex; flex-wrap: wrap; gap: 20px; justify-content: center; max-width: 300px;">
          <div style="text-align: center; padding: 12px 24px; background: #e6f7ff; border-radius: 8px;">
            <div style="font-size: 20px; font-weight: bold; color: #1890ff;">35%</div>
            <div style="font-size: 12px; color: #666;">美妆护肤</div>
          </div>
          <div style="text-align: center; padding: 12px 24px; background: #f6ffed; border-radius: 8px;">
            <div style="font-size: 20px; font-weight: bold; color: #52c41a;">28%</div>
            <div style="font-size: 12px; color: #666;">母婴用品</div>
          </div>
          <div style="text-align: center; padding: 12px 24px; background: #fff7e6; border-radius: 8px;">
            <div style="font-size: 20px; font-weight: bold; color: #fa8c16;">22%</div>
            <div style="font-size: 12px; color: #666;">保健品</div>
          </div>
          <div style="text-align: center; padding: 12px 24px; background: #f9f0ff; border-radius: 8px;">
            <div style="font-size: 20px; font-weight: bold; color: #722ed1;">15%</div>
            <div style="font-size: 12px; color: #666;">其他</div>
          </div>
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