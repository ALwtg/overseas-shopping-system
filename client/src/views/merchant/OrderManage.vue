<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">订单管理</h2>

    <!-- 订单统计 -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
      <el-card shadow="hover" v-for="stat in orderStats" :key="stat.label" class="text-center">
        <div class="text-2xl font-bold" :class="stat.color">{{ stat.value }}</div>
        <div class="text-sm text-gray-500 mt-1">{{ stat.label }}</div>
      </el-card>
    </div>

    <!-- 筛选栏 -->
    <el-card class="mb-6">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单编号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单编号" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="待评价" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 订单列表 -->
    <el-card>
      <div v-for="order in orders" :key="order.id" class="border rounded-lg mb-4 last:mb-0">
        <!-- 订单头部 -->
        <div class="bg-gray-50 px-4 py-3 flex items-center justify-between">
          <div class="flex items-center gap-6 text-sm">
            <span class="font-medium">订单号：{{ order.orderNo }}</span>
            <span class="text-gray-500">下单时间：{{ order.createTime }}</span>
            <span class="text-gray-500">买家：{{ order.username }}</span>
          </div>
          <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
        </div>

        <!-- 商品列表 -->
        <div class="p-4">
          <div v-for="(item, index) in order.items" :key="index" class="flex items-center gap-4 py-3" :class="index > 0 ? 'border-t' : ''">
            <img :src="item.productImage" class="w-16 h-16 object-cover rounded" :alt="item.productName">
            <div class="flex-1">
              <div class="font-medium">{{ item.productName }}</div>
              <div class="text-sm text-gray-500">x{{ item.quantity }}</div>
            </div>
            <div class="text-red-600">¥{{ item.price }}</div>
          </div>
        </div>

        <!-- 订单底部 -->
        <div class="bg-gray-50 px-4 py-3 flex items-center justify-between">
          <div>
            <span class="text-gray-600">收货人：{{ order.receiverName }} {{ order.receiverPhone }}</span>
            <span class="text-gray-500 ml-4">{{ order.address }}</span>
          </div>
          <div class="flex items-center gap-4">
            <span class="text-gray-600">实付金额：<span class="text-xl font-bold text-red-600">¥{{ order.totalAmount }}</span></span>
            <div class="flex gap-2">
              <el-button size="small" @click="viewDetail(order)">查看详情</el-button>
              <el-button v-if="order.status === 1" type="primary" size="small" @click="shipOrder(order)">发货</el-button>
            </div>
          </div>
        </div>
      </div>

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
    </el-card>

    <!-- 发货弹窗 -->
    <el-dialog v-model="shipDialogVisible" title="订单发货" width="400px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="物流公司" required>
          <el-select v-model="shipForm.carrier" placeholder="请选择物流公司" class="w-full">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="圆通快递" value="圆通快递" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="EMS" value="EMS" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" required>
          <el-input v-model="shipForm.trackingNo" placeholder="请输入物流单号" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="shipForm.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipping" @click="confirmShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const orderStats = ref([
  { label: '待付款', value: 3, color: 'text-yellow-600' },
  { label: '待发货', value: 5, color: 'text-blue-600' },
  { label: '待收货', value: 8, color: 'text-orange-600' },
  { label: '已完成', value: 156, color: 'text-green-600' }
])

const searchForm = ref({
  orderNo: '',
  status: null,
  dateRange: []
})

const orders = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const shipDialogVisible = ref(false)
const shipping = ref(false)
const currentOrder = ref(null)
const shipForm = ref({
  carrier: '',
  trackingNo: '',
  remark: ''
})

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
    // 模拟数据
    orders.value = [
      {
        id: 1,
        orderNo: 'DD202401150001',
        createTime: '2024-01-15 10:30:00',
        username: '张三',
        status: 1,
        receiverName: '张三',
        receiverPhone: '13800138000',
        address: '北京市朝阳区xxx街道xxx号',
        totalAmount: 1567.00,
        items: [
          { productName: 'SK-II神仙水', productImage: '/p1.jpg', quantity: 1, price: 1299 },
          { productName: '面膜', productImage: '/p2.jpg', quantity: 2, price: 134 }
        ]
      }
    ]
    total.value = 1
  } catch (error) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchOrders()
}

const resetSearch = () => {
  searchForm.value = {
    orderNo: '',
    status: null,
    dateRange: []
  }
  handleSearch()
}

const viewDetail = (order) => {
  // 查看订单详情
}

const shipOrder = (order) => {
  currentOrder.value = order
  shipForm.value = { carrier: '', trackingNo: '', remark: '' }
  shipDialogVisible.value = true
}

const confirmShip = async () => {
  if (!shipForm.value.carrier || !shipForm.value.trackingNo) {
    ElMessage.warning('请填写物流信息')
    return
  }
  shipping.value = true
  try {
    // 调用发货API
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    fetchOrders()
  } catch (error) {
    ElMessage.error('发货失败')
  } finally {
    shipping.value = false
  }
}

onMounted(fetchOrders)
</script>