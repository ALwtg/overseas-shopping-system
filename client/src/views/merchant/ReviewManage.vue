<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">评价管理</h2>

    <!-- 评价统计 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-yellow-500">{{ stats.averageRating }}</div>
        <div class="text-sm text-gray-500 mt-1">综合评分</div>
        <el-rate v-model="stats.averageRating" disabled class="mt-2" />
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-blue-500">{{ stats.totalReviews }}</div>
        <div class="text-sm text-gray-500 mt-1">总评价数</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-green-500">{{ stats.goodRate }}%</div>
        <div class="text-sm text-gray-500 mt-1">好评率</div>
      </el-card>
      <el-card shadow="hover" class="text-center">
        <div class="text-3xl font-bold text-red-500">{{ stats.pendingReply }}</div>
        <div class="text-sm text-gray-500 mt-1">待回复</div>
      </el-card>
    </div>

    <!-- 筛选 -->
    <el-card class="mb-6">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="评价等级">
          <el-select v-model="searchForm.rating" placeholder="全部" clearable>
            <el-option label="好评" :value="5" />
            <el-option label="中评" :value="3" />
            <el-option label="差评" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复状态">
          <el-select v-model="searchForm.replyStatus" placeholder="全部" clearable>
            <el-option label="已回复" :value="1" />
            <el-option label="未回复" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 评价列表 -->
    <el-card>
      <div v-for="review in reviews" :key="review.id" class="border-b last:border-0 py-6">
        <div class="flex items-start gap-4">
          <el-avatar :size="50" :src="review.userAvatar">
            {{ review.username?.charAt(0) }}
          </el-avatar>
          <div class="flex-1">
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center gap-3">
                <span class="font-medium">{{ review.username }}</span>
                <el-rate v-model="review.rating" disabled />
                <el-tag v-if="review.rating >= 4" type="success" size="small">好评</el-tag>
                <el-tag v-else-if="review.rating >= 3" type="warning" size="small">中评</el-tag>
                <el-tag v-else type="danger" size="small">差评</el-tag>
              </div>
              <span class="text-gray-400 text-sm">{{ review.createTime }}</span>
            </div>

            <div class="flex items-center gap-4 mb-3">
              <img :src="review.productImage" class="w-16 h-16 object-cover rounded">
              <div>
                <div class="text-sm text-gray-600">{{ review.productName }}</div>
                <div class="text-sm text-gray-400">订单号：{{ review.orderNo }}</div>
              </div>
            </div>

            <p class="text-gray-700 mb-3">{{ review.content }}</p>

            <div v-if="review.images" class="flex gap-2 mb-4">
              <el-image
                v-for="(img, idx) in review.images.split(',')"
                :key="idx"
                :src="img"
                :preview-src-list="review.images.split(',')"
                class="w-20 h-20 rounded cursor-pointer"
                fit="cover"
              />
            </div>

            <!-- 商家回复 -->
            <div v-if="review.merchantReply" class="bg-gray-50 rounded-lg p-4 mt-3">
              <div class="flex items-center gap-2 text-blue-600 mb-2">
                <el-icon><Shop /></el-icon>
                <span class="font-medium">商家回复</span>
                <span class="text-gray-400 text-sm">{{ review.replyTime }}</span>
              </div>
              <p class="text-gray-700">{{ review.merchantReply }}</p>
            </div>

            <div v-else class="mt-3">
              <el-button type="primary" size="small" @click="replyReview(review)">
                回复评价
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="flex justify-center mt-6">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @change="fetchReviews"
        />
      </div>
    </el-card>

    <!-- 回复弹窗 -->
    <el-dialog v-model="replyVisible" title="回复评价" width="500px">
      <div class="mb-4 p-4 bg-gray-50 rounded-lg">
        <div class="text-sm text-gray-500 mb-2">用户评价：</div>
        <p>{{ currentReview?.content }}</p>
      </div>
      <el-input
        v-model="replyContent"
        type="textarea"
        :rows="4"
        placeholder="请输入回复内容"
        maxlength="500"
        show-word-limit
      />
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" :loading="replying" @click="confirmReply">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Shop } from '@element-plus/icons-vue'

const stats = ref({
  averageRating: 4.5,
  totalReviews: 156,
  goodRate: 92,
  pendingReply: 8
})

const searchForm = ref({
  rating: null,
  replyStatus: null
})

const reviews = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const replyVisible = ref(false)
const replying = ref(false)
const currentReview = ref(null)
const replyContent = ref('')

const fetchReviews = async () => {
  try {
    reviews.value = [
      {
        id: 1,
        username: '张三',
        userAvatar: '',
        rating: 5,
        content: '商品质量很好，物流也很快，非常满意！',
        images: '/review1.jpg,/review2.jpg',
        createTime: '2024-01-15 10:30:00',
        productName: 'SK-II神仙水 230ml',
        productImage: '/product1.jpg',
        orderNo: 'DD202401150001',
        merchantReply: null
      },
      {
        id: 2,
        username: '李四',
        userAvatar: '',
        rating: 4,
        content: '东西不错，就是包装有点破损。',
        images: '',
        createTime: '2024-01-14 15:20:00',
        productName: '爱他美奶粉 3段',
        productImage: '/product2.jpg',
        orderNo: 'DD202401140002',
        merchantReply: '感谢您的反馈，我们会改进包装！',
        replyTime: '2024-01-14 16:00:00'
      }
    ]
    total.value = 2
  } catch (error) {
    ElMessage.error('获取评价失败')
  }
}

const handleSearch = () => {
  page.value = 1
  fetchReviews()
}

const resetSearch = () => {
  searchForm.value = { rating: null, replyStatus: null }
  handleSearch()
}

const replyReview = (review) => {
  currentReview.value = review
  replyContent.value = ''
  replyVisible.value = true
}

const confirmReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  replying.value = true
  try {
    ElMessage.success('回复成功')
    replyVisible.value = false
    fetchReviews()
  } catch (error) {
    ElMessage.error('回复失败')
  } finally {
    replying.value = false
  }
}

onMounted(fetchReviews)
</script>