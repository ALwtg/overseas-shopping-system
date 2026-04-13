<template>
  <div class="min-h-screen bg-gray-50">
    <AppHeader />
    
    <div class="container mx-auto px-4 py-8">
      <h1 class="text-2xl font-bold mb-6">我的收藏</h1>

      <div v-if="loading" class="text-center py-20">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="favorites.length === 0" class="bg-white rounded-lg shadow-md p-12 text-center">
        <el-empty description="暂无收藏商品">
          <el-button type="primary" @click="$router.push('/products')">去购物</el-button>
        </el-empty>
      </div>

      <template v-else>
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
          <div 
            v-for="item in favorites" 
            :key="item.id"
            class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-shadow group"
          >
            <div class="relative aspect-square overflow-hidden cursor-pointer" @click="viewProduct(item.productId)">
              <img 
                :src="item.productImage || '/default-product.jpg'" 
                :alt="item.productName"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              >
              <div class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity">
                <el-button 
                  circle 
                  type="danger" 
                  size="small"
                  @click.stop="removeFavorite(item.productId)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
            <div class="p-3">
              <div 
                class="font-medium text-gray-800 truncate cursor-pointer hover:text-blue-600"
                @click="viewProduct(item.productId)"
              >
                {{ item.productName }}
              </div>
              <div class="flex items-center justify-between mt-2">
                <span class="text-red-600 font-bold">¥{{ item.productPrice }}</span>
                <el-button type="primary" size="small" @click="addToCart(item)">
                  加入购物车
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="flex justify-center mt-8">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[12, 24, 36]"
            layout="total, sizes, prev, pager, next"
            @change="fetchFavorites"
          />
        </div>
      </template>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { getFavoriteList, removeFavorite } from '@/api/favorite'
import { addToCart as addToCartApi } from '@/api/cart'
import { useCartStore } from '@/store/useCartStore'

const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const favorites = ref([])
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavoriteList({
      page: page.value,
      pageSize: pageSize.value
    })
    favorites.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

const viewProduct = (productId) => {
  router.push(`/product/${productId}`)
}

const removeFavoriteItem = async (productId) => {
  try {
    await ElMessageBox.confirm('确定取消收藏该商品吗？', '提示', { type: 'warning' })
    await removeFavorite(productId)
    ElMessage.success('取消收藏成功')
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const addToCart = async (item) => {
  try {
    await addToCartApi({
      productId: item.productId,
      quantity: 1
    })
    cartStore.fetchCartCount()
    ElMessage.success('已加入购物车')
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

onMounted(fetchFavorites)
</script>