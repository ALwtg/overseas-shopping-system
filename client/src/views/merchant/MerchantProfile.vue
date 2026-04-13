<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">店铺信息</h2>

    <el-card class="max-w-3xl">
      <el-form :model="form" label-width="120px" class="py-4">
        <el-form-item label="店铺Logo">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleLogoSuccess"
          >
            <img v-if="form.logo" :src="form.logo" class="w-24 h-24 object-cover rounded">
            <div v-else class="w-24 h-24 border-2 border-dashed border-gray-300 rounded flex flex-col items-center justify-center text-gray-400">
              <el-icon class="text-2xl mb-1"><Plus /></el-icon>
              <span class="text-xs">上传Logo</span>
            </div>
          </el-upload>
        </el-form-item>

        <el-form-item label="店铺名称" required>
          <el-input v-model="form.name" placeholder="请输入店铺名称" maxlength="50" show-word-limit />
        </el-form-item>

        <el-form-item label="店铺简介">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入店铺简介"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="联系电话" required>
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="联系邮箱">
          <el-input v-model="form.email" placeholder="请输入联系邮箱" />
        </el-form-item>

        <el-form-item label="经营地址" required>
          <el-input v-model="form.address" placeholder="请输入经营地址" />
        </el-form-item>

        <el-form-item label="主营类目">
          <el-select v-model="form.category" multiple placeholder="请选择主营类目" class="w-full">
            <el-option label="美妆护肤" value="beauty" />
            <el-option label="母婴用品" value="baby" />
            <el-option label="保健品" value="health" />
            <el-option label="食品保健" value="food" />
            <el-option label="服饰箱包" value="fashion" />
          </el-select>
        </el-form-item>

        <el-form-item label="营业执照">
          <el-upload
            action="/api/upload"
            list-type="picture-card"
            :file-list="form.licenseImages"
            :on-success="handleLicenseSuccess"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="店铺公告">
          <el-input 
            v-model="form.notice" 
            type="textarea" 
            :rows="3"
            placeholder="请输入店铺公告，将在店铺首页展示"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="客服QQ">
          <el-input v-model="form.qq" placeholder="请输入客服QQ" />
        </el-form-item>

        <el-form-item label="客服微信">
          <el-input v-model="form.wechat" placeholder="请输入客服微信" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="saving" @click="saveProfile">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 店铺状态 -->
    <el-card class="mt-6 max-w-3xl">
      <template #header>
        <span class="font-bold">店铺状态</span>
      </template>
      <div class="flex items-center justify-between">
        <div>
          <div class="flex items-center gap-2 mb-2">
            <span class="text-gray-600">审核状态：</span>
            <el-tag type="success">已通过</el-tag>
          </div>
          <div class="flex items-center gap-2 mb-2">
            <span class="text-gray-600">店铺状态：</span>
            <el-tag type="success">正常运营</el-tag>
          </div>
          <div class="text-sm text-gray-500">
            入驻时间：2024-01-01
          </div>
        </div>
        <div class="text-right">
          <div class="text-sm text-gray-500 mb-1">店铺评分</div>
          <div class="text-3xl font-bold text-yellow-500">4.8</div>
          <el-rate v-model="rating" disabled />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const form = ref({
  name: '海外优选店',
  logo: '',
  description: '专注海外正品代购，为您提供优质的海外商品',
  phone: '13800138000',
  email: 'merchant@example.com',
  address: '上海市浦东新区xxx路xxx号',
  category: ['beauty', 'baby'],
  licenseImages: [],
  notice: '欢迎光临本店，全场满299包邮！',
  qq: '123456789',
  wechat: 'merchant_wechat'
})

const rating = ref(4.8)
const saving = ref(false)

const handleLogoSuccess = (res) => {
  form.value.logo = res.data
}

const handleLicenseSuccess = (res, file) => {
  form.value.licenseImages.push({
    name: file.name,
    url: res.data
  })
}

const saveProfile = async () => {
  if (!form.value.name || !form.value.phone || !form.value.address) {
    ElMessage.warning('请填写必填项')
    return
  }
  saving.value = true
  try {
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  // 获取店铺信息
})
</script>