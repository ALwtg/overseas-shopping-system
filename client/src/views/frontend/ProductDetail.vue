<template>
  <div class="min-h-screen bg-gray-50">
    <AppHeader />
    
    <div class="container mx-auto px-4 py-8">
      <!-- 面包屑 -->
      <el-breadcrumb class="mb-6">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/products' }">商品</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
      </el-breadcrumb>

      <div v-if="loading" class="text-center py-20">
        <el-skeleton :rows="10" animated />
      </div>

      <template v-else>
        <!-- 商品主信息 -->
        <div class="bg-white rounded-lg shadow-md p-6 mb-6">
          <div class="grid md:grid-cols-2 gap-8">
            <!-- 商品图片 -->
            <div class="aspect-square rounded-lg overflow-hidden bg-gray-100">
              <img 
                :src="product.coverImage || '/product.png'" 
                :alt="product.name"
                class="w-full h-full object-cover"
              >
            </div>

            <!-- 商品信息 -->
            <div class="flex flex-col">
              <h1 class="text-2xl font-bold text-gray-800 mb-4">{{ product.name }}</h1>
              <p class="text-gray-600 mb-4">{{ product.description }}</p>
              
              <div class="text-3xl font-bold text-red-600 mb-2">
                ¥{{ product.price }}
              </div>
              <div class="text-gray-500 line-through mb-4">
                ¥{{ product.originalPrice }}
              </div>

              <div class="space-y-3 text-sm text-gray-600 mb-6">
                <div class="flex items-center gap-2">
                  <span class="w-20 text-gray-500">品牌：</span>
                  <span>{{ product.brand }}</span>
                </div>
                <div class="flex items-center gap-2">
                  <span class="w-20 text-gray-500">产地：</span>
                  <span>{{ product.originCountry }}</span>
                </div>
                <div class="flex items-center gap-2">
                  <span class="w-20 text-gray-500">规格：</span>
                  <span>{{ product.specification }}</span>
                </div>
                <div class="flex items-center gap-2">
                  <span class="w-20 text-gray-500">库存：</span>
                  <span :class="product.stock > 0 ? 'text-green-600' : 'text-red-600'">
                    {{ product.stock > 0 ? '有货' : '缺货' }}
                  </span>
                </div>
              </div>

              <!-- 数量选择 -->
              <div class="flex items-center gap-4 mb-6">
                <span class="text-gray-600">数量：</span>
                <el-input-number v-model="quantity" :min="1" :max="product.stock" />
              </div>

              <!-- 操作按钮 -->
              <div class="flex gap-4 mt-auto">
                <el-button 
                  type="primary" 
                  size="large" 
                  :disabled="product.stock === 0"
                  @click="addToCart"
                >
                  <el-icon class="mr-1"><ShoppingCart /></el-icon>
                  加入购物车
                </el-button>
                <el-button 
                  type="danger" 
                  size="large"
                  :disabled="product.stock === 0"
                  @click="buyNow"
                >
                  立即购买
                </el-button>
                <el-button 
                  :type="isFavorite ? 'warning' : 'default'" 
                  size="large"
                  @click="toggleFavorite"
                >
                  <el-icon class="mr-1"><Star /></el-icon>
                  {{ isFavorite ? '已收藏' : '收藏' }}
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 商品详情和评论 -->
        <el-tabs v-model="activeTab" class="bg-white rounded-lg shadow-md">
          <el-tab-pane label="商品详情" name="detail">
            <div class="p-6">
              <div class="prose max-w-none" v-html="product.detail"></div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="商品评价" name="reviews">
            <div class="p-6">
              <div v-if="reviews.length === 0" class="text-center text-gray-500 py-10">
                暂无评价
              </div>
              <div v-else class="space-y-6">
                <div v-for="review in reviews" :key="review.id" class="border-b pb-6 last:border-0">
                  <div class="flex items-center gap-3 mb-3">
                    <el-avatar :size="40" :src="review.userAvatar">
                      {{ review.username?.charAt(0) }}
                    </el-avatar>
                    <div>
                      <div class="font-medium">{{ review.username }}</div>
                      <el-rate v-model="review.rating" disabled show-score />
                    </div>
                    <span class="text-gray-400 text-sm ml-auto">{{ review.createTime }}</span>
                  </div>
                  <p class="text-gray-700">{{ review.content }}</p>
                  <div v-if="review.images" class="flex gap-2 mt-3">
                    <img 
                      v-for="img in review.images.split(',')" 
                      :key="img"
                      :src="img" 
                      class="w-20 h-20 object-cover rounded"
                    >
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </template>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Star } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { getProductDetail } from '@/api/product'
import { addToCart as addToCartApi } from '@/api/cart'
import { addFavorite, checkFavorite, removeFavorite } from '@/api/favorite'
import { getProductReviews } from '@/api/review'
import { useUserStore } from '@/store/useUserStore'
import { useCartStore } from '@/store/useCartStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const loading = ref(true)
const product = ref({})
const quantity = ref(1)
const isFavorite = ref(false)
const activeTab = ref('detail')
const reviews = ref([])

const fetchProduct = async () => {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
    if (userStore.token) {
      checkIsFavorite()
    }
    fetchReviews()
  } catch (error) {
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

const checkIsFavorite = async () => {
  try {
    const res = await checkFavorite(route.params.id)
    isFavorite.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const fetchReviews = async () => {
  try {
    const res = await getProductReviews(route.params.id)
    reviews.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const addToCart = async () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await addToCartApi({
      productId: product.value.id,
      quantity: quantity.value
    })
    cartStore.fetchCartCount()
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error('[加入购物车失败]', error)
    if (error.message && error.message.includes('异常类型:')) {
      // 显示服务器返回的详细错误
      console.error('[服务器详细错误]', error.message)
    }
    ElMessage.error('添加失败，请查看控制台')
  }
}

const buyNow = () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push({
    path: '/order/confirm',
    query: { productId: product.value.id, quantity: quantity.value }
  })
}

const toggleFavorite = async () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    if (isFavorite.value) {
      await removeFavorite(product.value.id)
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite({ productId: product.value.id })
      ElMessage.success('收藏成功')
    }
    isFavorite.value = !isFavorite.value
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(fetchProduct)
</script>