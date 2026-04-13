<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">用户管理</h2>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-blue-600">{{ stats.total }}</div>
        <div class="text-sm text-gray-500 mt-1">总用户数</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-green-600">{{ stats.active }}</div>
        <div class="text-sm text-gray-500 mt-1">活跃用户</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-yellow-600">{{ stats.newToday }}</div>
        <div class="text-sm text-gray-500 mt-1">今日新增</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-red-600">{{ stats.disabled }}</div>
        <div class="text-sm text-gray-500 mt-1">已禁用</div>
      </el-card>
    </div>

    <!-- 筛选栏 -->
    <el-card class="mb-6">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户名/手机号">
          <el-input v-model="searchForm.keyword" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="用户状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="注册时间">
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

    <!-- 用户列表 -->
    <el-card>
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column type="index" width="50" />
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <el-avatar :size="40" :src="row.avatar">
                {{ row.username?.charAt(0) }}
              </el-avatar>
              <div>
                <div class="font-medium">{{ row.username }}</div>
                <div class="text-sm text-gray-500">{{ row.phone }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 0 ? 'info' : row.role === 1 ? 'warning' : 'danger'">
              {{ row.role === 0 ? '普通用户' : row.role === 1 ? '商家' : '管理员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
            <el-button link :type="row.status === 1 ? 'danger' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="deleteUser(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="flex justify-center mt-6">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="fetchUsers"
        />
      </div>
    </el-card>

    <!-- 用户详情弹窗 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <div v-if="currentUser" class="space-y-4">
        <div class="flex items-center gap-4">
          <el-avatar :size="80" :src="currentUser.avatar">
            {{ currentUser.username?.charAt(0) }}
          </el-avatar>
          <div>
            <div class="text-xl font-bold">{{ currentUser.username }}</div>
            <div class="text-gray-500">{{ currentUser.email }}</div>
          </div>
        </div>
        <el-divider />
        <div class="grid grid-cols-2 gap-4">
          <div><span class="text-gray-500">手机号：</span>{{ currentUser.phone }}</div>
          <div><span class="text-gray-500">角色：</span>{{ currentUser.role === 0 ? '普通用户' : currentUser.role === 1 ? '商家' : '管理员' }}</div>
          <div><span class="text-gray-500">状态：</span><el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">{{ currentUser.status === 1 ? '正常' : '禁用' }}</el-tag></div>
          <div><span class="text-gray-500">注册时间：</span>{{ currentUser.createTime }}</div>
        </div>
        <el-divider />
        <div class="grid grid-cols-3 gap-4 text-center">
          <div class="p-4 bg-gray-50 rounded">
            <div class="text-2xl font-bold text-blue-600">{{ currentUser.orderCount || 0 }}</div>
            <div class="text-sm text-gray-500">订单数</div>
          </div>
          <div class="p-4 bg-gray-50 rounded">
            <div class="text-2xl font-bold text-green-600">¥{{ currentUser.totalSpent || 0 }}</div>
            <div class="text-sm text-gray-500">消费总额</div>
          </div>
          <div class="p-4 bg-gray-50 rounded">
            <div class="text-2xl font-bold text-orange-600">{{ currentUser.reviewCount || 0 }}</div>
            <div class="text-sm text-gray-500">评价数</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const stats = ref({
  total: 3256,
  active: 2890,
  newToday: 45,
  disabled: 23
})

const searchForm = ref({
  keyword: '',
  status: null,
  dateRange: []
})

const users = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const detailVisible = ref(false)
const currentUser = ref(null)

const fetchUsers = async () => {
  loading.value = true
  try {
    users.value = [
      {
        id: 1,
        username: '张三',
        avatar: '',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        role: 0,
        status: 1,
        createTime: '2024-01-01 10:30:00',
        orderCount: 15,
        totalSpent: '3,250.00',
        reviewCount: 8
      },
      {
        id: 2,
        username: '李四',
        avatar: '',
        phone: '13800138002',
        email: 'lisi@example.com',
        role: 1,
        status: 1,
        createTime: '2024-01-02 15:20:00',
        orderCount: 0,
        totalSpent: '0.00',
        reviewCount: 0
      }
    ]
    total.value = 2
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchUsers()
}

const resetSearch = () => {
  searchForm.value = {
    keyword: '',
    status: null,
    dateRange: []
  }
  handleSearch()
}

const viewDetail = (row) => {
  currentUser.value = row
  detailVisible.value = true
}

const toggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定${action}该用户吗？`, '提示', { type: 'warning' })
    ElMessage.success(`${action}成功`)
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const deleteUser = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该用户吗？删除后不可恢复！', '警告', { type: 'warning' })
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(fetchUsers)
</script>