<template>
  <div class="min-h-screen bg-gradient-to-br from-primary-dark via-primary to-blue-400 flex items-center justify-center px-4 pt-16">
    <div class="w-full max-w-md">
      <!-- Logo & Title -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-white/20 backdrop-blur-sm mb-4">
          <Globe2 class="w-9 h-9 text-white" />
        </div>
        <h1 class="text-3xl font-bold text-white">欢迎登录</h1>
        <p class="text-blue-100 mt-2">海外代购平台 - 全球好物一站购齐</p>
      </div>

      <!-- Login Form -->
      <div class="bg-white rounded-2xl shadow-2xl p-8 space-y-5">
        <el-form ref="formRef" :model="form" :rules="rules" size="large">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码"
                      prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
          </el-form-item>

          <el-button type="primary" :loading="loading" class="w-full py-6 text-base font-semibold rounded-xl"
                     @click="handleLogin">
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form>

        <div class="flex items-center justify-between text-sm pt-2">
          <span class="text-gray-500">还没有账号？<a href="/register" class="text-primary hover:underline font-medium">立即注册</a></span>
        </div>

        <!-- Demo Accounts -->
        <div class="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-xl p-4 mt-4 border border-blue-100">
          <p class="text-xs font-semibold text-gray-600 mb-2">演示账号（点击自动填充）</p>
          <div class="flex flex-wrap gap-2">
            <button v-for="acc in demoAccounts" :key="acc.username"
                    @click="fillAccount(acc)" class="px-3 py-1.5 text-xs rounded-lg bg-white border border-blue-200
                            text-gray-700 hover:bg-primary hover:text-white hover:border-primary transition-all">
              {{ acc.label }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Globe2 } from 'lucide-vue-next'
import { useUserStore } from '@/store/useUserStore'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const demoAccounts = [
  { label: '管理员 admin123', username: 'admin', password: 'admin123' },
  { label: '用户 user123', username: 'xiaoming', password: 'user123' },
  { label: '商家 user123', username: 'zhangsan', password: 'user123' },
]

function fillAccount(acc) {
  form.username = acc.username
  form.password = acc.password
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success('登录成功！')
    if (userStore.isAdmin) router.push('/admin/dashboard')
    else if (userStore.isMerchant) router.push('/merchant/home')
    else router.push('/')
  } catch (e) {}
  loading.value = false
}
</script>
