<template>
  <div class="min-h-screen bg-gray-50">
    <AppHeader />
    
    <div class="container mx-auto px-4 py-8">
      <h1 class="text-2xl font-bold mb-6">我的订单</h1>

      <!-- 状态筛选 -->
      <div class="bg-white rounded-lg shadow-md p-4 mb-6">
        <el-radio-group v-model="statusFilter" @change="handleStatusChange">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="0">待付款</el-radio-button>
          <el-radio-button label="1">待发货</el-radio-button>
          <el-radio-button label="2">待收货</el-radio-button>
          <el-radio-button label="3">待评价</el-radio-button>
          <el-radio-button label="4">已完成</el-radio-button>
          <el-radio-button label="5">已取消</el-radio-button>
        </el-radio-group>
      </div>

      <div v-if="loading" class="text-center py-20">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="orders.length === 0" class="bg-white rounded-lg shadow-md p-12 text-center">
        <el-empty :description="`暂无${getStatusText(statusFilter)}订单`">
          <el-button type="primary" @click="$router.push('/products')">去购物</el-button>
        </el-empty>
      </div>

      <div v-else class="space-y-4">
        <div 
          v-for="order in orders" 
          :key="order.id"
          class="bg-white rounded-lg shadow-md overflow-hidden"
        >
          <!-- 订单头部 -->
          <div class="bg-gray-50 px-6 py-3 flex items-center justify-between">
            <div class="flex items-center gap-4 text-sm text-gray-600">
              <span>订单号：{{ order.orderNo }}</span>
              <span>下单时间：{{ order.createTime }}</span>
            </div>
            <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
          </div>

          <!-- 商品列表 -->
          <div class="p-6">
            <div 
              v-for="(item, index) in order.items" 
              :key="index"
              class="flex items-center gap-4 py-4"
              :class="index > 0 ? 'border-t' : ''"
            >
              <img 
                :src="item.productImage" 
                class="w-20 h-20 object-cover rounded"
                :alt="item.productName"
              >
              <div class="flex-1">
                <div class="font-medium">{{ item.productName }}</div>
                <div class="text-sm text-gray-500">x{{ item.quantity }}</div>
              </div>
              <div class="text-red-600">¥{{ item.price }}</div>
            </div>
          </div>

          <!-- 订单底部 -->
          <div class="bg-gray-50 px-6 py-4 flex items-center justify-between">
            <div>
              <span class="text-gray-600">实付金额：</span>
              <span class="text-xl font-bold text-red-600">¥{{ order.totalAmount }}</span>
            </div>
            <div class="flex gap-2">
              <el-button @click="viewDetail(order.id)">查看详情</el-button>
              <template v-if="order.status === 0">
                <el-button type="primary" @click="payOrder(order.id)">立即支付</el-button>
                <el-button type="danger" link @click="cancelOrder(order.id)">取消订单</el-button>
              </template>
              <el-button 
                v-if="order.status === 2" 
                type="primary"
                @click="confirmReceipt(order.id)"
              >
                确认收货
              </el-button>
              <el-button 
                v-if="order.status === 3" 
                type="primary"
                @click="goReview(order.id)"
              >
                去评价
              </el-button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="flex justify-center mt-6">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @change="fetchOrders"
          />
        </div>
      </div>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { getOrderList, cancelOrder as cancelOrderApi, confirmReceipt as confirmReceiptApi } from '@/api/order'

const router = useRouter()

const loading = ref(false)
const orders = ref([])
const statusFilter = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getStatusType = (status) => {
  const types = ['warning', 'primary', 'success', 'info', 'success', 'info']
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = ['待付款', '待发货', '待收货', '待评价', '已完成', '已取消']
  return texts[status] || '未知'
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await getOrderList({
      status: statusFilter.value,
      page: page.value,
      pageSize: pageSize.value
    })
    orders.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

const handleStatusChange = () => {
  page.value = 1
  fetchOrders()
}

const viewDetail = (id) => {
  router.push(`/order/detail/${id}`)
}

const payOrder = (id) => {
  ElMessage.info('跳转支付页面...')
  // 实际项目中跳转到支付页面
}

const cancelOrder = async (id) => {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrderApi(id)
    ElMessage.success('取消成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

const confirmReceipt = async (id) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'warning' })
    await confirmReceiptApi(id)
    ElMessage.success('确认成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认失败')
    }
  }
}

const goReview = (id) => {
  router.push(`/order/review/${id}`)
}

onMounted(fetchOrders)
</script>