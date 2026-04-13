<template>
  <div class="space-y-0 py-2" v-if="list && list.length">
    <div v-for="(item, index) in list" :key="index"
         class="flex gap-3 relative pb-4 last:pb-0">
      <!-- 时间线 -->
      <div class="flex flex-col items-center flex-shrink-0 w-8">
        <div :class="[
          'w-3 h-3 rounded-full border-2 z-10',
          index === 0 ? 'bg-primary border-primary scale-110' : 'bg-white border-gray-300'
        ]"></div>
        <div v-if="index !== list.length - 1"
             class="w-0.5 flex-1 mt-1" :class="index === 0 ? 'bg-primary/30' : 'bg-gray-200'"></div>
      </div>

      <!-- 内容 -->
      <div class="flex-1 min-w-0 -mt-0.5">
        <p class="text-sm font-medium text-gray-800">{{ item.nodeName }}</p>
        <p v-if="item.description" class="text-xs text-gray-500 mt-0.5">{{ item.description }}</p>
        <p v-if="item.location" class="text-xs text-primary mt-0.5">{{ item.location }}</p>
        <p class="text-xs text-gray-400 mt-0.5">{{ formatTime(item.time) }}</p>
      </div>
    </div>
  </div>
  <div v-else class="py-8 text-center text-gray-400 text-sm">
    暂无物流信息
  </div>
</template>

<script setup>
defineProps({ list: { type: Array, default: () => [] } })

function formatTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}
</script>
