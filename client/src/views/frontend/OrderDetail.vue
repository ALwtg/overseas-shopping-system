<template>
  <div class="min-h-screen bg-gray-50">
    <AppHeader />
    
    <div class="container mx-auto px-4 py-8">
      <div class="mb-6">
        <el-button link @click="$router.back()">
          <el-icon class="mr-1"><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>

      <div v-if="loading" class="text-center py-20">
        <el-skeleton :rows="10" animated />
      </div>

      <template v-else>
        <div class="grid lg:grid-cols-3 gap-6">
          <!-- 左侧主要内容 -->
          <div class="lg:col-span-2 space-y-6">
            <!-- 订单状态 -->
            <div class="bg-white rounded-lg shadow-md p-6">
              <div class="flex items-center justify-between mb-4">
                <h2 class="text-lg font-medium">订单状态</h2>
                <el-tag :type="getStatusType(order.status)" size="large">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </div>
              <el-steps :active="getStepActive(order.status)" finish-status="success">
                <el-step title="提交订单" />
                <el-step title="付款成功" />
                <el-step title="商家发货" />
                <el-step title="确认收货" />
                <el-step title="完成评价" />
              </el-steps>
            </div>

            <!-- 物流信息 -->
            <div v-if="order.status >= 2" class="bg-white rounded-lg shadow-md p-6">
              <div class="flex items-center justify-between mb-4">
                <h2 class="text-lg font-medium">物流信息</h2>
                <el-button v-if="logistics.length > 0" link type="primary" @click="showLogistics = true">
                  查看全部
                </el-button>
              </div>
              <LogisticsTimeline v-if="logistics.length > 0" :logistics="logistics.slice(0, 3)" />
              <div v-else class="text-center text-gray-500 py-4">
                暂无物流信息
              </div>
            </div>

            <!-- 商品清单 -->
            <div class="bg-white rounded-lg shadow-md p-6">
              <h2 class="text-lg font-medium mb-4">商品清单</h2>
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
                  @click="$router.push(`/product/${item.productId}`)"
                  style="cursor: pointer"
                >
                <div class="flex-1">
                  <div 
                    class="font-medium cursor-pointer hover:text-blue-600"
                    @click="$router.push(`/product/${item.productId}`)"
                  >
                    {{ item.productName }}
                  </div>
                  <div class="text-sm text-gray-500">数量：x{{ item.quantity }}</div>
                </div>
                <div class="text-red-600">¥{{ item.price }}</div>
              </div>
            </div>

            <!-- 订单信息 -->
            <div class="bg-white rounded-lg shadow-md p-6">
              <h2 class="text-lg font-medium mb-4">订单信息</h2>
              <div class="grid md:grid-cols-2 gap-4 text-sm">
                <div>
                  <span class="text-gray-500">订单编号：</span>
                  <span>{{ order.orderNo }}</span>
                </div>
                <div>
                  <span class="text-gray-500">下单时间：</span>
                  <span>{{ order.createTime }}</span>
                </div>
                <div v-if="order.payTime">
                  <span class="text-gray-500">付款时间：</span>
                  <span>{{ order.payTime }}</span>
                </div>
                <div v-if="order.shipTime">
                  <span class="text-gray-500">发货时间：</span>
                  <span>{{ order.shipTime }}</span>
                </div>
                <div v-if="order.receiveTime">
                  <span class="text-gray-500">收货时间：</span>
                  <span>{{ order.receiveTime }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧信息 -->
          <div class="space-y-6">
            <!-- 收货信息 -->
            <div class="bg-white rounded-lg shadow-md p-6">
              <h2 class="text-lg font-medium mb-4">收货信息</h2>
              <div class="space-y-2 text-sm">
                <div>
                  <span class="text-gray-500">收货人：</span>
                  <span>{{ order.receiverName }}</span>
                </div>
                <div>
                  <span class="text-gray-500">手机号：</span>
                  <span>{{ order.receiverPhone }}</span>
                </div>
                <div>
                  <span class="text-gray-500">收货地址：</span>
                  <span>{{ order.address }}</span>
                </div>
              </div>
            </div>

            <!-- 金额信息 -->
            <div class="bg-white rounded-lg shadow-md p-6">
              <h2 class="text-lg font-medium mb-4">金额明细</h2>
              <div class="space-y-2 text-sm">
                <div class="flex justify-between">
                  <span class="text-gray-500">商品总价</span>
                  <span>¥{{ order.productAmount }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-gray-500">运费</span>
                  <span>¥{{ order.shippingFee }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-gray-500">优惠</span>
                  <span>-¥{{ order.discountAmount }}</span>
                </div>
                <el-divider />
                <div class="flex justify-between text-lg">
                  <span class="font-medium">实付金额</span>
                  <span class="font-bold text-red-600">¥{{ order.totalAmount }}</span>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="bg-white rounded-lg shadow-md p-6">
              <div class="space-y-2">
                <template v-if="order.status === 0">
                  <el-button type="danger" class="w-full" @click="payOrder">立即支付</el-button>
                  <el-button class="w-full" @click="cancelOrder">取消订单</el-button>
                </template>
                <el-button 
                  v-if="order.status === 2" 
                  type="primary" 
                  class="w-full"
                  @click="confirmReceipt"
                >
                  确认收货
                </el-button>
                <el-button 
                  v-if="order.status === 3" 
                  type="primary" 
                  class="w-full"
                  @click="goReview"
                >
                  去评价
                </el-button>
                <el-button class="w-full" @click="contactService">联系客服</el-button>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 物流详情弹窗 -->
    <el-dialog v-model="showLogistics" title="物流详情" width="600px">
      <div v-if="logistics.length > 0">
        <div class="mb-4">
          <span class="text-gray-600">物流公司：</span>
          <span>{{ logistics[0].carrier }}</span>
        </div>
        <div class="mb-4">
          <span class="text-gray-600">物流单号：</span>
          <span>{{ logistics[0].trackingNo }}</span>
        </div>
        <LogisticsTimeline :logistics="logistics" />
      </div>
    </el-dialog>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import LogisticsTimeline from '@/components/LogisticsTimeline.vue'
import { getOrderDetail, cancelOrder as cancelOrderApi, confirmReceipt as confirmReceiptApi } from '@/api/order'
import { getOrderLogistics } from '@/api/logistics'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const order = ref({ items: [] })
const logistics = ref([])
const showLogistics = ref(false)

const getStatusType = (status) => {
  const types = ['warning', 'primary', 'success', 'info', 'success', 'info']
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = ['待付款', '待发货', '待收货', '待评价', '已完成', '已取消']
  return texts[status] || '未知'
}

const getStepActive = (status) => {
  if (status === 5) return 0
  return status + 1
}

const fetchOrder = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data
    if (order.value.status >= 2) {
      fetchLogistics()
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const fetchLogistics = async () => {
  try {
    const res = await getOrderLogistics(route.params.id)
    logistics.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const payOrder = () => {
  ElMessage.info('跳转支付页面...')
}

const cancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrderApi(order.value.id)
    ElMessage.success('取消成功')
    fetchOrder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

const confirmReceipt = async () => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'warning' })
    await confirmReceiptApi(order.value.id)
    ElMessage.success('确认成功')
    fetchOrder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认失败')
    }
  }
}

const goReview = () => {
  router.push(`/order/review/${order.value.id}`)
}

const contactService = () => {
  ElMessage.info('客服功能开发中...')
}

onMounted(fetchOrder)
</script>