<template>
  <div class="min-h-screen bg-gray-50">
    <AppHeader />
    
    <div class="container mx-auto px-4 py-8">
      <h1 class="text-2xl font-bold mb-6">收货地址管理</h1>

      <div class="bg-white rounded-lg shadow-md p-6">
        <div class="flex justify-between items-center mb-6">
          <span class="text-gray-600">共 {{ addresses.length }} 个地址</span>
          <el-button type="primary" @click="showAddDialog">
            <el-icon class="mr-1"><Plus /></el-icon>
            添加新地址
          </el-button>
        </div>

        <div v-if="addresses.length === 0" class="text-center py-12 text-gray-500">
          <el-empty description="暂无收货地址" />
        </div>

        <div v-else class="grid md:grid-cols-2 gap-4">
          <div 
            v-for="addr in addresses" 
            :key="addr.id"
            class="border rounded-lg p-5 relative"
            :class="addr.isDefault ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-gray-300'"
          >
            <div class="flex items-start justify-between mb-3">
              <div class="flex items-center gap-3">
                <span class="font-medium text-lg">{{ addr.receiverName }}</span>
                <span class="text-gray-600">{{ addr.receiverPhone }}</span>
              </div>
              <el-tag v-if="addr.isDefault" type="success" size="small">默认地址</el-tag>
            </div>
            
            <p class="text-gray-600 mb-4">
              {{ addr.province }} {{ addr.city }} {{ addr.district }}<br>
              {{ addr.detailAddress }}
            </p>

            <div class="flex items-center gap-4 pt-4 border-t">
              <el-button link type="primary" @click="editAddress(addr)">
                <el-icon class="mr-1"><Edit /></el-icon>
                编辑
              </el-button>
              <el-button link type="danger" @click="deleteAddressItem(addr.id)">
                <el-icon class="mr-1"><Delete /></el-icon>
                删除
              </el-button>
              <el-button 
                v-if="!addr.isDefault" 
                link 
                type="success" 
                @click="setDefaultAddress(addr.id)"
              >
                设为默认
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑地址弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="form.id ? '编辑地址' : '添加新地址'" 
      width="500px"
      destroy-on-close
    >
      <el-form 
        ref="formRef"
        :model="form" 
        :rules="rules"
        label-width="90px"
        class="mt-4"
      >
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="所在地区" prop="region">
          <el-cascader 
            v-model="form.region" 
            :options="regionOptions"
            placeholder="请选择省/市/区"
            class="w-full"
            :props="{ checkStrictly: false }"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input 
            v-model="form.detailAddress" 
            type="textarea" 
            :rows="3"
            placeholder="请输入街道、门牌号等详细地址"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.isDefault">设为默认收货地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress as setDefaultAddressApi } from '@/api/user'

const addresses = ref([])
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = ref({
  receiverName: '',
  receiverPhone: '',
  region: [],
  detailAddress: '',
  isDefault: false
})

const rules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  region: [{ required: true, message: '请选择所在地区', trigger: 'change' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const regionOptions = [
  { value: '北京市', label: '北京市', children: [{ value: '北京市', label: '北京市', children: [{ value: '朝阳区', label: '朝阳区' }, { value: '海淀区', label: '海淀区' }] }] },
  { value: '上海市', label: '上海市', children: [{ value: '上海市', label: '上海市', children: [{ value: '浦东新区', label: '浦东新区' }, { value: '黄浦区', label: '黄浦区' }] }] },
  { value: '广东省', label: '广东省', children: [{ value: '广州市', label: '广州市', children: [{ value: '天河区', label: '天河区' }] }, { value: '深圳市', label: '深圳市', children: [{ value: '南山区', label: '南山区' }] }] },
  { value: '浙江省', label: '浙江省', children: [{ value: '杭州市', label: '杭州市', children: [{ value: '西湖区', label: '西湖区' }] }, { value: '宁波市', label: '宁波市', children: [{ value: '海曙区', label: '海曙区' }] }] },
  { value: '江苏省', label: '江苏省', children: [{ value: '南京市', label: '南京市', children: [{ value: '玄武区', label: '玄武区' }] }, { value: '苏州市', label: '苏州市', children: [{ value: '姑苏区', label: '姑苏区' }] }] },
]

const fetchAddresses = async () => {
  try {
    const res = await getAddressList()
    addresses.value = res.data || []
  } catch (error) {
    ElMessage.error('获取地址列表失败')
  }
}

const showAddDialog = () => {
  form.value = {
    receiverName: '',
    receiverPhone: '',
    region: [],
    detailAddress: '',
    isDefault: false
  }
  dialogVisible.value = true
}

const editAddress = (addr) => {
  form.value = {
    ...addr,
    region: [addr.province, addr.city, addr.district]
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const data = {
      ...form.value,
      province: form.value.region[0],
      city: form.value.region[1],
      district: form.value.region[2]
    }
    if (form.value.id) {
      await updateAddress(form.value.id, data)
    } else {
      await addAddress(data)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchAddresses()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

const deleteAddressItem = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该地址吗？', '提示', { type: 'warning' })
    await deleteAddress(id)
    ElMessage.success('删除成功')
    fetchAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const setDefaultAddress = async (id) => {
  try {
    await setDefaultAddressApi(id)
    ElMessage.success('设置成功')
    fetchAddresses()
  } catch (error) {
    ElMessage.error('设置失败')
  }
}

onMounted(fetchAddresses)
</script>