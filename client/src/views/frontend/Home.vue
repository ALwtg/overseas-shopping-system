<template>
  <div class="min-h-screen pt-[104px]">
    <AppHeader />
    <!-- Hero Banner -->
    <section class="relative overflow-hidden bg-gradient-to-r from-primary-dark via-primary to-blue-400 text-white">
      <div class="absolute inset-0 opacity-10">
        <div class="absolute top-10 left-10 w-72 h-72 rounded-full bg-white blur-3xl"></div>
        <div class="absolute bottom-10 right-20 w-96 h-96 rounded-full bg-cyan-300 blur-3xl"></div>
      </div>

      <div class="max-w-7xl mx-auto px-4 py-16 relative z-10">
        <div class="grid md:grid-cols-2 gap-12 items-center">
          <div class="space-y-6">
            <h1 class="text-4xl md:text-5xl font-bold leading-tight">
              全球好物<br/>
              <span class="text-accent">一站购齐</span>
            </h1>
            <p class="text-lg text-blue-100 leading-relaxed max-w-lg">
              甄选全球优质商品，专业代购团队为您保驾护航。美国、日本、欧洲直邮，正品保障，全程可追踪。
            </p>
            <div class="flex gap-4">
              <button @click="$router.push('/products')"
                      class="px-8 py-3.5 bg-accent hover:bg-orange-600 text-white font-semibold rounded-xl transition-all shadow-lg shadow-orange-500/30 hover:shadow-orange-500/50">
                立即选购
              </button>
              <button @click="$router.push('/register')"
                      class="px-8 py-3.5 border-2 border-white/40 hover:bg-white/10 text-white font-semibold rounded-xl transition-all">
                免费注册
              </button>
            </div>

            <div class="flex gap-8 pt-4">
              <div><span class="text-2xl font-bold">10000+</span><p class="text-sm text-blue-200 mt-1">精选商品</p></div>
              <div><span class="text-2xl font-bold">500+</span><p class="text-sm text-blue-200 mt-1">合作品牌</p></div>
              <div><span class="text-2xl font-bold">98%</span><p class="text-sm text-blue-200 mt-1">好评率</p></div>
            </div>
          </div>

          <div class="relative hidden md:block">
            <img src="https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=600&h=500&fit=crop"
                 alt="Shopping" class="rounded-2xl shadow-2xl transform rotate-2 hover:rotate-0 transition-transform duration-500" />
          </div>
        </div>
      </div>
    </section>

    <!-- Category Navigation -->
    <section class="max-w-7xl mx-auto px-4 -mt-8 relative z-20 mb-12">
      <div class="bg-white rounded-2xl shadow-lg p-6 grid grid-cols-4 md:grid-cols-8 gap-3">
        <button v-for="(cat, index) in categories" :key="index"
                @click="$router.push({ path: '/products', query: { categoryId: cat.id } })"
                class="group flex flex-col items-center p-3 rounded-xl hover:bg-primary/5 transition-all cursor-pointer">
          <span class="text-2xl mb-1.5 group-hover:scale-110 transition-transform">{{ cat.icon }}</span>
          <span class="text-xs text-gray-600 group-hover:text-primary font-medium">{{ cat.name }}</span>
        </button>
      </div>
    </section>

    <!-- Hot Products -->
    <section class="max-w-7xl mx-auto px-4 pb-16">
      <div class="flex items-center justify-between mb-6">
        <div>
          <h2 class="text-2xl font-bold text-gray-800">热销推荐</h2>
          <p class="text-gray-500 text-sm mt-1">精选海外爆款，限时特惠</p>
        </div>
        <button @click="$router.push('/products')" class="text-primary hover:text-primary-dark font-medium text-sm flex items-center gap-1">
          查看全部 <ChevronRight class="w-4 h-4" />
        </button>
      </div>

      <div v-if="hotProducts.length" class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4">
        <ProductCard v-for="product in hotProducts" :key="product.id" :product="product" />
      </div>
      <div v-else class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4">
        <ProductCard v-for="i in 10" :key="i"
                     :product="{ id: i, name: '热门代购商品', price: (Math.random() * 2000 + 100).toFixed(2), sales: Math.floor(Math.random() * 500), isHot: 1, sourceCountry: ['美国','日本','韩国'][i % 3], coverImage: `https://images.unsplash.com/photo-${1511499767150 + a48d23aafdcf}?w=400&h=400&fit=crop` }" />
      </div>
    </section>

    <!-- Announcements -->
    <section v-if="announcements.length" class="max-w-7xl mx-auto px-4 pb-12">
      <div class="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-xl p-6 border border-blue-100">
        <h3 class="font-semibold text-gray-800 mb-3 flex items-center gap-2">
          <Bell class="w-5 h-5 text-primary" /> 平台公告
        </h3>
        <div class="space-y-2">
          <div v-for="item in announcements.slice(0, 3)" :key="item.id"
               class="bg-white rounded-lg p-3.5 hover:shadow-md transition-shadow cursor-pointer">
            <p class="font-medium text-sm text-gray-800">{{ item.title }}</p>
            <p class="text-xs text-gray-500 mt-1 line-clamp-1">{{ item.content?.replace(/<[^>]+>/g, '') }}</p>
          </div>
        </div>
      </div>
    </section>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import ProductCard from '@/components/ProductCard.vue'
import { ChevronRight, Bell } from 'lucide-vue-next'
import { getHotProducts } from '@/api/product'
import { getCategoryTree } from '@/api/category'
import { getAnnouncementList } from '@/api/announcement'

const hotProducts = ref([])
const categories = ref([
  { id: 1, name: '美妆护肤', icon: '💄' }, { id: 2, name: '数码电子', icon: '📱' },
  { id: 3, name: '时尚服饰', icon: '👗' }, { id: 4, name: '母婴用品', icon: '🍼' },
  { id: 5, name: '食品保健', icon: '🥜' }, { id: 6, name: '运动户外', icon: '⚽' },
  { id: 7, name: '家居生活', icon: '🏠' }, { id: 8, name: '奢侈品', icon: '💎' }
])
const announcements = ref([])

onMounted(async () => {
  try {
    const res = await getHotProducts()
    hotProducts.value = res.data?.records || []
  } catch (e) {}
  try {
    const res = await getAnnouncementList()
    announcements.value = res.data || []
  } catch (e) {}
})
</script>
