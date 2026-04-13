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

      <h1 class="text-2xl font-bold mb-6">订单评价</h1>

      <div v-if="loading" class="text-center py-20">
        <el-skeleton :rows="5" animated />
      </div>

      <template v-else>
        <div class="bg-white rounded-lg shadow-md p-6 mb-6">
          <div class="text-gray-600 mb-4">订单号：{{ order.orderNo }}</div>
          
          <div v-for="(item, index) in order.items" :key="index" class="border-b last:border-0 py-6">
            <div class="flex items-center gap-4 mb-4">
              <img :src="item.productImage" class="w-16 h-16 object-cover rounded" :alt="item.productName">
              <div>
                <div class="font-medium">{{ item.productName }}</div>
                <div class="text-sm text-gray-500">x{{ item.quantity }}</div>
              </div>
            </div>

            <div class="pl-20">
              <div class="flex items-center gap-4 mb-4">
                <span class="text-gray-600">商品评分：</span>
                <el-rate v-model="reviews[index].rating" show-score />
              </div>
              <el-input
                v-model="reviews[index].content"
                type="textarea"
                :rows="4"
                placeholder="请输入评价内容，分享您的购物体验"
                maxlength="500"
                show-word-limit
              />
              <div class="mt-4">
                <el-upload
                  v-model:file-list="reviews[index].images"
                  action="/api/upload"
                  list-type="picture-card"
                  :limit="6"
                  :on-success="(res) => handleUploadSuccess(res, index)"
                >
                  <el-icon><Plus /></el-icon>
                </el-upload>
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-center gap-4">
          <el-button size="large" @click="$router.back()">取消</el-button>
          <el-button type="primary" size="large" :loading="submitting" @click="submitReview">
            提交评价
          </el-button>
        </div>
      </template>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { getOrderDetail } from '@/api/order'
import { submitReview as submitReviewApi } from '@/api/review'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
const order = ref({ items: [] })
const reviews = ref([])

const fetchOrder = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data
    reviews.value = order.value.items.map(() => ({
      rating: 5,
      content: '',
      images: []
    }))
  } catch (error) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

const handleUploadSuccess = (response, index) => {
  // 处理上传成功
}

const submitReview = async () => {
  // 验证所有评价
  for (let i = 0; i < reviews.value.length; i++) {
    if (!reviews.value[i].content.trim()) {
      ElMessage.warning(`请填写第${i + 1}件商品的评价内容`)
      return
    }
  }

  submitting.value = true
  try {
    const promises = order.value.items.map((item, index) => {
      return submitReviewApi({
        orderId: order.value.id,
        productId: item.productId,
        rating: reviews.value[index].rating,
        content: reviews.value[index].content,
        images: reviews.value[index].images.map(f => f.response?.data || f.url).join(',')
      })
    })
    await Promise.all(promises)
    ElMessage.success('评价提交成功')
    router.push('/orders')
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(fetchOrder)
</script>