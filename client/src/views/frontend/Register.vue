<template>
  <div class="min-h-screen bg-gradient-to-br from-primary-dark via-blue-600 to-cyan-400 flex items-center justify-center px-4 pt-16">
    <div class="w-full max-w-md">
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-white/20 backdrop-blur-sm mb-4">
          <UserPlus class="w-9 h-9 text-white" />
        </div>
        <h1 class="text-3xl font-bold text-white">创建账号</h1>
        <p class="text-blue-100 mt-2">加入海外代购平台，开启全球购物之旅</p>
      </div>

      <div class="bg-white rounded-2xl shadow-2xl p-8 space-y-5">
        <el-form ref="formRef" :model="form" :rules="rules" size="large" label-position="top">
          <el-form-item prop="username" label="用户名">
            <el-input v-model="form.username" placeholder="请输入用户名（用于登录）" />
          </el-form-item>

          <el-form-item prop="nickname" label="昵称">
            <el-input v-model="form.nickname" placeholder="您的昵称" />
          </el-form-item>

          <el-form-item prop="phone" label="手机号">
            <el-input v-model="form.phone" placeholder="请输入手机号码" />
          </el-form-item>

          <el-form-item prop="password" label="密码">
            <el-input v-model="form.password" type="password" placeholder="6-20位字符"
                      show-password />
          </el-form-item>

          <el-button type="primary" :loading="loading" class="w-full py-5 text-base font-semibold rounded-xl"
                     @click="handleRegister">
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
        </el-form>

        <div class="text-center text-sm pt-2 text-gray-500">
          已有账号？<a href="/login" class="text-primary hover:underline font-medium">返回登录</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { UserPlus } from 'lucide-vue-next'
import { useUserStore } from '@/store/useUserStore'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', nickname: '', phone: '', password: '' })
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '3-20个字符', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '6-20位字符', trigger: 'blur' }
  ],
}

async function handleRegister() {
  try {
    await formRef.value.validate()
    loading.value = true
    await userStore.register(form)
    ElMessage.success('注册成功！请登录')
    router.push('/login')
  } catch (e) {
    ElMessage.error(e.message || '注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>
