import axios from 'axios'
import { useUserStore } from '@/store/useUserStore'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

request.interceptors.request.use(config => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
}, error => Promise.reject(error))

request.interceptors.response.use(response => {
  const res = response.data
  if (res.code !== 200) {
    ElMessage.error(res.msg || '请求失败')
    if (res.code === 401) {
      const userStore = useUserStore()
      userStore.logout()
      window.location.href = '/login'
    }
    return Promise.reject(new Error(res.msg || 'Error'))
  }
  return res
}, error => {
  console.error('请求错误:', error)
  ElMessage.error(error.message || '网络异常')
  return Promise.reject(error)
})

export default request
