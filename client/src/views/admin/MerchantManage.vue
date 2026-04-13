<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">商家管理</h2>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-blue-600">{{ stats.total }}</div>
        <div class="text-sm text-gray-500 mt-1">入驻商家</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-green-600">{{ stats.active }}</div>
        <div class="text-sm text-gray-500 mt-1">正常运营</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-yellow-600">{{ stats.pending }}</div>
        <div class="text-sm text-gray-500 mt-1">待审核</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-red-600">{{ stats.disabled }}</div>
        <div class="text-sm text-gray-500 mt-1">已封禁</div>
      </el-card>
    </div>

    <!-- 筛选栏 -->
    <el-card class="mb-6">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="店铺名称">
          <el-input v-model="searchForm.name" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
            <el-option label="已封禁" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 商家列表 -->
    <el-card>
      <el-table :data="merchants" v-loading="loading" stripe>
        <el-table-column type="index" width="50" />
        <el-table-column label="店铺信息" min-width="250">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <img :src="row.logo || '/default-shop.png'" class="w-12 h-12 object-cover rounded">
              <div>
                <div class="font-medium">{{ row.name }}</div>
                <div class="text-sm text-gray-500">{{ row.contactPhone }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="店主" width="120">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <el-avatar :size="24" :src="row.ownerAvatar">
                {{ row.ownerName?.charAt(0) }}
              </el-avatar>
              <span>{{ row.ownerName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="主营类目" width="150" />
        <el-table-column label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
            <template v-if="row.status === 0">
              <el-button link type="success" @click="approve(row)">通过</el-button>
              <el-button link type="danger" @click="reject(row)">拒绝</el-button>
            </template>
            <el-button v-else-if="row.status === 1" link type="danger" @click="ban(row)">封禁</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="flex justify-center mt-6">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @change="fetchMerchants"
        />
      </div>
    </el-card>

    <!-- 商家详情/审核弹窗 -->
    <el-dialog v-model="detailVisible" title="商家详情" width="700px">
      <div v-if="currentMerchant" class="space-y-4">
        <div class="flex items-start gap-6">
          <img :src="currentMerchant.logo || '/default-shop.png'" class="w-24 h-24 object-cover rounded">
          <div class="flex-1">
            <h3 class="text-xl font-bold">{{ currentMerchant.name }}</h3>
            <el-tag :type="getStatusType(currentMerchant.status)" class="mt-2">{{ getStatusText(currentMerchant.status) }}</el-tag>
          </div>
        </div>
        <el-divider />
        <div class="grid grid-cols-2 gap-4">
          <div><span class="text-gray-500">店主：</span>{{ currentMerchant.ownerName }}</div>
          <div><span class="text-gray-500">联系电话：</span>{{ currentMerchant.contactPhone }}</div>
          <div><span class="text-gray-500">主营类目：</span>{{ currentMerchant.category }}</div>
          <div><span class="text-gray-500">申请时间：</span>{{ currentMerchant.createTime }}</div>
        </div>
        <el-divider />
        <div>
          <div class="text-gray-500 mb-2">店铺简介：</div>
          <p class="text-gray-700">{{ currentMerchant.description || '暂无简介' }}</p>
        </div>
        <el-divider />
        <div>
          <div class="text-gray-500 mb-2">营业执照：</div>
          <div class="flex gap-4">
            <img v-for="(img, idx) in currentMerchant.licenseImages" :key="idx" :src="img" class="w-32 h-24 object-cover rounded cursor-pointer" @click="previewImage(img)">
          </div>
        </div>
      </div>
      <template #footer v-if="currentMerchant?.status === 0">
        <el-button @click="detailVisible = false">取消</el-button>
        <el-button type="danger" @click="reject(currentMerchant)">拒绝</el-button>
        <el-button type="success" @click="approve(currentMerchant)">通过审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const stats = ref({
  total: 128,
  active: 115,
  pending: 5,
  disabled: 8
})

const searchForm = ref({
  name: '',
  status: null
})

const merchants = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const detailVisible = ref(false)
const currentMerchant = ref(null)

const getStatusType = (status) => {
  const types = ['warning', 'success', 'danger', 'info']
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = ['待审核', '已通过', '已拒绝', '已封禁']
  return texts[status] || '未知'
}

const fetchMerchants = async () => {
  loading.value = true
  try {
    merchants.value = [
      {
        id: 1,
        name: '海外优选店',
        logo: '',
        ownerName: '张三',
        ownerAvatar: '',
        contactPhone: '13800138000',
        category: '美妆护肤',
        status: 0,
        createTime: '2024-01-15 10:30:00',
        description: '专注海外正品代购',
        licenseImages: ['/license1.jpg', '/license2.jpg']
      },
      {
        id: 2,
        name: '母婴生活馆',
        logo: '',
        ownerName: '李四',
        ownerAvatar: '',
        contactPhone: '13800138001',
        category: '母婴用品',
        status: 1,
        createTime: '2024-01-10 14:20:00'
      }
    ]
    total.value = 2
  } catch (error) {
    ElMessage.error('获取商家列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchMerchants()
}

const resetSearch = () => {
  searchForm.value = { name: '', status: null }
  handleSearch()
}

const viewDetail = (row) => {
  currentMerchant.value = row
  detailVisible.value = true
}

const approve = async (row) => {
  try {
    await ElMessageBox.confirm('确定通过该商家入驻申请吗？', '确认', { type: 'warning' })
    ElMessage.success('审核通过')
    detailVisible.value = false
    fetchMerchants()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

const reject = async (row) => {
  try {
    await ElMessageBox.confirm('确定拒绝该商家入驻申请吗？', '确认', { type: 'warning' })
    ElMessage.success('已拒绝')
    detailVisible.value = false
    fetchMerchants()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

const ban = async (row) => {
  try {
    await ElMessageBox.confirm('确定封禁该商家吗？封禁后商家将无法继续经营！', '警告', { type: 'warning' })
    ElMessage.success('已封禁')
    fetchMerchants()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

const previewImage = (url) => {
  // 预览图片
}

onMounted(fetchMerchants)
</script>