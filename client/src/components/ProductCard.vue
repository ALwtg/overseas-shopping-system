<template>
  <div class="group bg-white rounded-xl overflow-hidden shadow-sm hover:shadow-lg transition-all duration-300 border border-gray-100 hover:border-primary/30 cursor-pointer" @click="$router.push(`/product/${product.id}`)">
    <!-- 商品图片 -->
    <div class="relative aspect-square overflow-hidden bg-gray-50">
      <img :src="product.coverImage || '/product.png'"
           :alt="product.name"
           class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
           loading="lazy" />
      <span v-if="product.isHot === 1" class="absolute top-2 left-2 bg-accent text-white text-xs px-2 py-0.5 rounded-full font-medium">
        热销
      </span>
      <span v-if="product.sourceCountry" class="absolute top-2 right-2 bg-white/90 backdrop-blur-sm text-xs px-2 py-0.5 rounded-full font-medium text-gray-700 flex items-center gap-1">
        <Globe class="w-3 h-3" /> {{ product.sourceCountry }}
      </span>
    </div>

    <!-- 商品信息 -->
    <div class="p-3.5 space-y-1.5">
      <h3 class="text-sm font-medium line-clamp-2 leading-snug group-hover:text-primary transition-colors">{{ product.name }}</h3>

      <div class="flex items-baseline gap-1.5 pt-0.5">
        <span class="text-lg font-bold text-accent">¥{{ product.price?.toLocaleString() }}</span>
        <span v-if="product.originPrice > 0" class="text-xs text-gray-400 line-through">¥{{ product.originPrice?.toLocaleString() }}</span>
      </div>

      <div class="flex items-center justify-between pt-1">
        <span class="text-xs text-gray-400">已售 {{ product.sales || 0 }} 件</span>
        <button class="opacity-0 group-hover:opacity-100 transition-opacity p-1.5 rounded-full bg-primary/10 text-primary hover:bg-primary hover:text-white" @click.stop>
          <ShoppingCart class="w-4 h-4" />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ShoppingCart, Globe } from 'lucide-vue-next'
defineProps({ product: { type: Object, required: true } })
</script>
