<template>
  <div class="min-h-screen pt-[104px] bg-gray-50">
    <AppHeader />
    <div class="max-w-7xl mx-auto px-4 py-8">
      <!-- 搜索栏 -->
      <div class="bg-white rounded-xl shadow-sm p-4 mb-6 flex items-center gap-4">
        <div class="flex-1 relative">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input v-model="keyword" type="text" placeholder="搜索海外商品、品牌..."
                 class="w-full pl-10 pr-4 py-2.5 border border-gray-200 rounded-lg focus:border-primary focus:ring-1 focus:ring-primary outline-none text-sm"
                 @keyup.enter="fetchData" />
        </div>
        <button @click="showFilters = !showFilters" :class="['px-4 py-2.5 rounded-lg text-sm font-medium transition-colors', showFilters ? 'bg-primary text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200']">
          <Filter class="w-4 h-4 inline mr-1" /> 筛选
        </button>
        <select v-model="sort" @change="fetchData" class="px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:border-primary">
          <option value="">默认排序</option>
          <option value="sales_desc">销量优先</option>
          <option value="price_asc">价格从低到高</option>
          <option value="price_desc">价格从高到低</option>
        </select>
      </div>

      <!-- 分类标签 -->
      <div class="flex gap-2 mb-6 overflow-x-auto pb-2">
        <button @click="selectedCategory = 0; fetchData()"
                :class="['px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-all', selectedCategory === 0 ? 'bg-primary text-white' : 'bg-white text-gray-600 hover:bg-gray-100']">
          全部
        </button>
        <button v-for="cat in categoryList" :key="cat.id"
                @click="selectedCategory = cat.id; fetchData()"
                :class="['px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-all', selectedCategory === cat.id ? 'bg-primary text-white' : 'bg-white text-gray-600 hover:bg-gray-100']">
          {{ cat.icon }} {{ cat.name }}
        </button>
      </div>

      <!-- 商品网格 -->
      <div v-if="products.length" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4 mb-8">
        <ProductCard v-for="p in products" :key="p.id" :product="p" />
      </div>
      <div v-else-if="!loading" class="py-20 text-center text-gray-400">
        <Package class="w-16 h-16 mx-auto mb-4 opacity-30" />
        <p>暂无相关商品</p>
      </div>

      <!-- 分页 -->
      <div v-if="total > size" class="flex justify-center pb-8">
        <el-pagination background layout="prev, pager, next" :total="total"
                       :page-size="size" v-model:current-page="page" @current-change="onPageChange" />
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import ProductCard from '@/components/ProductCard.vue'
import { Search, Filter, Package } from 'lucide-vue-next'
import { getProductList } from '@/api/product'
import { getCategoryTree } from '@/api/category'

const route = useRoute()
const products = ref([])
const categoryList = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const size = ref(12)
const keyword = ref('')
const sort = ref('')
const showFilters = ref(false)
const selectedCategory = ref(0)

async function fetchData() {
  loading.value = true
  try {
    const res = await getProductList({ page: page.value, size: size.value,
      categoryId: selectedCategory.value || undefined, keyword: keyword.value || undefined })
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {}
  loading.value = false
}

function onPageChange(p) { page.value = p; fetchData() }

onMounted(async () => {
  try {
    const res = await getCategoryTree()
    if (res.data) categoryList.value = res.data.flatMap(c => [c, ...(c.children || [])])
  } catch (e) {}

  if (route.query.keyword) keyword.value = route.query.keyword
  if (route.query.categoryId) selectedCategory.value = Number(route.query.categoryId)
  fetchData()
})
</script>
