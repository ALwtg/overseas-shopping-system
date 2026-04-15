import axios from 'axios'
import { useUserStore } from '@/store/useUserStore'
import { ElMessage } from 'element-plus'

console.log('[API] Request module version: 1.0.2')
console.log('[API] Base URL:', '/api')
console.log('[API] Current host:', window.location.host)

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
  if (Number(res.code) !== 200) {
    // 500错误显示详细堆栈信息
    if (Number(res.code) === 500 && res.msg) {
      console.error('[服务器错误详情]\n', res.msg)
      ElMessage.error('服务器错误，请查看控制台详细日志')
    } else {
      ElMessage.error(res.msg || '请求失败')
    }
    if (Number(res.code) === 401) {
      const userStore = useUserStore()
      userStore.logout()
      window.location.href = '/login'
    }
    return Promise.reject(new Error(res.msg || 'Error'))
  }
  return res
}, error => {
  console.error('请求错误:', error)
  // 如果有服务器返回的详细错误信息，也输出到控制台
  if (error.response?.data?.msg) {
    console.error('[服务器错误详情]\n', error.response.data.msg)
  }
  ElMessage.error(error.message || '网络异常')
  return Promise.reject(error)
})

export default request
