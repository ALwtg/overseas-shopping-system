<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">物流管理</h2>

    <!-- 批量发货 -->
    <el-card class="mb-6">
      <template #header>
        <span class="font-bold">批量发货</span>
      </template>
      <el-form :inline="true">
        <el-form-item label="选择订单">
          <el-select 
            v-model="batchShipForm.orderIds" 
            multiple 
            placeholder="请选择待发货订单"
            style="width: 300px"
          >
            <el-option 
              v-for="order in pendingOrders" 
              :key="order.id" 
              :label="order.orderNo" 
              :value="order.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="物流公司">
          <el-select v-model="batchShipForm.carrier" placeholder="请选择">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="圆通快递" value="圆通快递" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="韵达快递" value="韵达快递" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="batchShipping" @click="batchShip">批量发货</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 物流列表 -->
    <el-card>
      <template #header>
        <div class="flex items-center justify-between">
          <span class="font-bold">物流记录</span>
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索订单号/物流单号" 
            style="width: 250px"
            clearable
          >
            <template #suffix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </template>

      <el-table :data="logisticsList" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="carrier" label="物流公司" width="120" />
        <el-table-column prop="trackingNo" label="物流单号" width="180" />
        <el-table-column label="当前状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="latestUpdate" label="最新动态" min-width="250" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">查看详情</el-button>
            <el-button link type="primary" @click="updateLogistics(row)">更新物流</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="flex justify-center mt-6">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @change="fetchLogistics"
        />
      </div>
    </el-card>

    <!-- 物流详情弹窗 -->
    <el-dialog v-model="detailVisible" title="物流详情" width="600px">
      <div v-if="currentLogistics" class="space-y-4">
        <div class="flex gap-8 text-sm">
          <div><span class="text-gray-500">订单号：</span>{{ currentLogistics.orderNo }}</div>
          <div><span class="text-gray-500">物流公司：</span>{{ currentLogistics.carrier }}</div>
          <div><span class="text-gray-500">物流单号：</span>{{ currentLogistics.trackingNo }}</div>
        </div>
        <el-divider />
        <LogisticsTimeline :logistics="currentLogistics.timeline" />
      </div>
    </el-dialog>

    <!-- 更新物流弹窗 -->
    <el-dialog v-model="updateVisible" title="更新物流信息" width="500px">
      <el-form :model="updateForm" label-width="100px">
        <el-form-item label="物流状态">
          <el-select v-model="updateForm.status" placeholder="请选择状态" class="w-full">
            <el-option label="已揽件" :value="1" />
            <el-option label="运输中" :value="2" />
            <el-option label="到达目的城市" :value="3" />
            <el-option label="派送中" :value="4" />
            <el-option label="已签收" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流描述">
          <el-input 
            v-model="updateForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入物流状态描述"
          />
        </el-form-item>
        <el-form-item label="所在城市">
          <el-input v-model="updateForm.location" placeholder="请输入当前所在城市" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="updateVisible = false">取消</el-button>
        <el-button type="primary" :loading="updating" @click="confirmUpdate">确认更新</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import LogisticsTimeline from '@/components/LogisticsTimeline.vue'

const loading = ref(false)
const logisticsList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

const pendingOrders = ref([
  { id: 1, orderNo: 'DD202401150001' },
  { id: 2, orderNo: 'DD202401150002' }
])

const batchShipForm = ref({
  orderIds: [],
  carrier: ''
})
const batchShipping = ref(false)

const detailVisible = ref(false)
const currentLogistics = ref(null)

const updateVisible = ref(false)
const updating = ref(false)
const updateForm = ref({
  status: null,
  description: '',
  location: ''
})

const getStatusType = (status) => {
  const types = { 1: 'primary', 2: 'warning', 3: 'warning', 4: 'success', 5: 'success' }
  return types[status] || 'info'
}

const fetchLogistics = async () => {
  loading.value = true
  try {
    logisticsList.value = [
      {
        id: 1,
        orderNo: 'DD202401150001',
        carrier: '顺丰速运',
        trackingNo: 'SF1234567890',
        status: 2,
        statusText: '运输中',
        latestUpdate: '快件已到达【北京转运中心】',
        updateTime: '2024-01-15 14:30:00',
        timeline: [
          { status: '已揽件', description: '顺丰速运已收取快件', location: '上海', createTime: '2024-01-15 09:00:00' },
          { status: '运输中', description: '快件已到达【北京转运中心】', location: '北京', createTime: '2024-01-15 14:30:00' }
        ]
      }
    ]
    total.value = 1
  } catch (error) {
    ElMessage.error('获取物流信息失败')
  } finally {
    loading.value = false
  }
}

const batchShip = async () => {
  if (batchShipForm.value.orderIds.length === 0 || !batchShipForm.value.carrier) {
    ElMessage.warning('请选择订单和物流公司')
    return
  }
  batchShipping.value = true
  try {
    ElMessage.success('批量发货成功')
    batchShipForm.value = { orderIds: [], carrier: '' }
    fetchLogistics()
  } catch (error) {
    ElMessage.error('批量发货失败')
  } finally {
    batchShipping.value = false
  }
}

const viewDetail = (row) => {
  currentLogistics.value = row
  detailVisible.value = true
}

const updateLogistics = (row) => {
  currentLogistics.value = row
  updateForm.value = { status: null, description: '', location: '' }
  updateVisible.value = true
}

const confirmUpdate = async () => {
  if (!updateForm.value.status || !updateForm.value.description) {
    ElMessage.warning('请填写完整信息')
    return
  }
  updating.value = true
  try {
    ElMessage.success('更新成功')
    updateVisible.value = false
    fetchLogistics()
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    updating.value = false
  }
}

onMounted(fetchLogistics)
</script>