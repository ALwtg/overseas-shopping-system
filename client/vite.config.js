import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 8080,
    host: '0.0.0.0',
    allowedHosts: true,
    proxy: {
      // 图片请求直接代理，不重写路径
      '/api/images': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
      // API 请求需要去掉 /api 前缀
      '/api': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/uploads': {
        target: 'http://localhost:9090',
        changeOrigin: true
      }
    }
  }
})
