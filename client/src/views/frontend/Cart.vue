<template>
  <div class="min-h-screen bg-gray-50">
    <AppHeader />
    
    <div class="container mx-auto px-4 py-8">
      <h1 class="text-2xl font-bold mb-6">购物车</h1>

      <div v-if="loading" class="text-center py-20">
        <el-skeleton :rows="5" animated />
      </div>

      <template v-else>
        <div v-if="cartItems.length === 0" class="bg-white rounded-lg shadow-md p-12 text-center">
          <el-empty description="购物车是空的">
            <el-button type="primary" @click="$router.push('/products')">去购物</el-button>
          </el-empty>
        </div>

        <template v-else>
          <div class="bg-white rounded-lg shadow-md overflow-hidden mb-6">
            <el-table :data="cartItems" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="55" />
              <el-table-column label="商品信息" min-width="300">
                <template #default="{ row }">
                  <div class="flex items-center gap-4">
                    <img 
                      :src="row.productImage" 
                      class="w-20 h-20 object-cover rounded"
                      :alt="row.productName"
                    >
                    <div>
                      <div class="font-medium text-gray-800">{{ row.productName }}</div>
                      <div class="text-sm text-gray-500">{{ row.productBrand }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="单价" width="120">
                <template #default="{ row }">
                  <span class="text-red-600 font-medium">¥{{ row.productPrice }}</span>
                </template>
              </el-table-column>
              <el-table-column label="数量" width="150">
                <template #default="{ row }">
                  <el-input-number 
                    v-model="row.quantity" 
                    :min="1" 
                    :max="99"
                    size="small"
                    @change="(val) => updateQuantity(row.id, val)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="小计" width="120">
                <template #default="{ row }">
                  <span class="text-red-600 font-bold">¥{{ (row.productPrice * row.quantity).toFixed(2) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button type="danger" link @click="removeItem(row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 结算栏 -->
          <div class="bg-white rounded-lg shadow-md p-4 flex items-center justify-between sticky bottom-4">
            <div class="flex items-center gap-4">
              <el-checkbox v-model="selectAll" @change="handleSelectAll">
                全选
              </el-checkbox>
              <el-button link type="danger" @click="removeSelected">
                删除选中
              </el-button>
            </div>
            <div class="flex items-center gap-6">
              <div>
                <span class="text-gray-600">已选 {{ selectedItems.length }} 件商品，合计：</span>
                <span class="text-2xl font-bold text-red-600">¥{{ totalPrice.toFixed(2) }}</span>
              </div>
              <el-button 
                type="danger" 
                size="large" 
                :disabled="selectedItems.length === 0"
                @click="checkout"
              >
                去结算
              </el-button>
            </div>
          </div>
        </template>
      </template>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { getCartList, updateCartItem, deleteCartItem, deleteCartItems } from '@/api/cart'
import { useCartStore } from '@/store/useCartStore'

const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const cartItems = ref([])
const selectedItems = ref([])
const selectAll = ref(false)

const totalPrice = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    return sum + item.productPrice * item.quantity
  }, 0)
})

const fetchCart = async () => {
  loading.value = true
  try {
    const res = await getCartList()
    cartItems.value = res.data || []
  } catch (error) {
    ElMessage.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (selection) => {
  selectedItems.value = selection
  selectAll.value = selection.length === cartItems.value.length && cartItems.value.length > 0
}

const handleSelectAll = (val) => {
  // 这里需要通过el-table的toggleAllSelection方法实现
}

const updateQuantity = async (id, quantity) => {
  try {
    await updateCartItem(id, { quantity })
    cartStore.fetchCartCount()
  } catch (error) {
    ElMessage.error('更新失败')
    fetchCart()
  }
}

const removeItem = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？', '提示', { type: 'warning' })
    await deleteCartItem(id)
    ElMessage.success('删除成功')
    cartStore.fetchCartCount()
    fetchCart()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const removeSelected = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedItems.value.length} 件商品吗？`, '提示', { type: 'warning' })
    const ids = selectedItems.value.map(item => item.id)
    await deleteCartItems(ids)
    ElMessage.success('删除成功')
    cartStore.fetchCartCount()
    fetchCart()
    selectedItems.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const checkout = () => {
  const ids = selectedItems.value.map(item => item.id)
  router.push({
    path: '/order/confirm',
    query: { cartItemIds: ids.join(',') }
  })
}

onMounted(fetchCart)
</script>