<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-2xl font-bold">商品管理</h2>
      <el-button type="primary" @click="$router.push('/merchant/products/add')">
        <el-icon class="mr-1"><Plus /></el-icon>
        发布商品
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <el-card class="mb-6">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-cascader 
            v-model="searchForm.categoryId" 
            :options="categoryOptions"
            placeholder="请选择分类"
            clearable
            :props="{ checkStrictly: true, value: 'id', label: 'name' }"
          />
        </el-form-item>
        <el-form-item label="商品状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="上架中" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon class="mr-1"><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 商品列表 -->
    <el-card>
      <el-table :data="products" v-loading="loading" stripe>
        <el-table-column label="商品信息" min-width="300">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <img :src="row.image" class="w-16 h-16 object-cover rounded" :alt="row.name">
              <div>
                <div class="font-medium">{{ row.name }}</div>
                <div class="text-sm text-gray-500">{{ row.categoryName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="价格" width="120">
          <template #default="{ row }">
            <div class="text-red-600">¥{{ row.price }}</div>
            <div class="text-gray-400 line-through text-sm">¥{{ row.originalPrice }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="sold" label="销量" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架中' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="editProduct(row)">编辑</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button link type="danger" @click="deleteProduct(row)">删除</el-button>
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
          @change="fetchProducts"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)
const products = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({
  name: '',
  categoryId: null,
  status: null
})

const categoryOptions = ref([
  { id: 1, name: '美妆护肤', children: [{ id: 11, name: '面部护理' }, { id: 12, name: '彩妆' }] },
  { id: 2, name: '母婴用品', children: [{ id: 21, name: '奶粉' }, { id: 22, name: '纸尿裤' }] },
  { id: 3, name: '保健品', children: [{ id: 31, name: '维生素' }, { id: 32, name: '蛋白粉' }] },
])

const fetchProducts = async () => {
  loading.value = true
  try {
    // 模拟数据
    products.value = [
      {
        id: 1,
        name: 'SK-II神仙水 230ml',
        image: '/product1.jpg',
        categoryName: '面部护理',
        price: 1299.00,
        originalPrice: 1599.00,
        stock: 100,
        sold: 258,
        status: 1,
        createTime: '2024-01-15 10:30:00'
      },
      {
        id: 2,
        name: '爱他美奶粉 3段',
        image: '/product2.jpg',
        categoryName: '奶粉',
        price: 268.00,
        originalPrice: 328.00,
        stock: 50,
        sold: 89,
        status: 1,
        createTime: '2024-01-14 15:20:00'
      }
    ]
    total.value = 2
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchProducts()
}

const resetSearch = () => {
  searchForm.value = {
    name: '',
    categoryId: null,
    status: null
  }
  handleSearch()
}

const editProduct = (row) => {
  router.push(`/merchant/products/edit/${row.id}`)
}

const toggleStatus = async (row) => {
  const action = row.status === 1 ? '下架' : '上架'
  try {
    await ElMessageBox.confirm(`确定${action}该商品吗？`, '提示', { type: 'warning' })
    // 调用API
    ElMessage.success(`${action}成功`)
    fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const deleteProduct = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？删除后不可恢复！', '警告', { type: 'warning' })
    // 调用API
    ElMessage.success('删除成功')
    fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(fetchProducts)
</script>