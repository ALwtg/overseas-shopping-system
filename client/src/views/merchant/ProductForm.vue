<template>
  <div>
    <div class="flex items-center gap-4 mb-6">
      <el-button link @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h2 class="text-2xl font-bold">{{ isEdit ? '编辑商品' : '发布商品' }}</h2>
    </div>

    <el-card class="max-w-4xl">
      <el-form 
        ref="formRef"
        :model="form" 
        :rules="rules"
        label-width="100px"
        class="py-4"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="商品分类" prop="categoryId">
          <el-cascader 
            v-model="form.categoryId" 
            :options="categoryOptions"
            placeholder="请选择商品分类"
            class="w-full"
            :props="{ checkStrictly: false, value: 'id', label: 'name' }"
          />
        </el-form-item>

        <el-form-item label="商品图片" prop="image">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleImageSuccess"
          >
            <img v-if="form.image" :src="form.image" class="w-40 h-40 object-cover rounded">
            <div v-else class="w-40 h-40 border-2 border-dashed border-gray-300 rounded flex flex-col items-center justify-center text-gray-400 hover:border-blue-500">
              <el-icon class="text-3xl mb-2"><Plus /></el-icon>
              <span>点击上传</span>
            </div>
          </el-upload>
          <div class="text-sm text-gray-500 mt-2">建议尺寸 800x800 像素，大小不超过 2MB</div>
        </el-form-item>

        <el-form-item label="商品详情图">
          <el-upload
            action="/api/upload"
            list-type="picture-card"
            :file-list="form.detailImages"
            :on-success="handleDetailImageSuccess"
            :on-remove="handleDetailImageRemove"
            multiple
            :limit="8"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <div class="grid grid-cols-2 gap-4">
          <el-form-item label="售价" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" class="w-full" />
          </el-form-item>
          <el-form-item label="原价" prop="originalPrice">
            <el-input-number v-model="form.originalPrice" :min="0" :precision="2" class="w-full" />
          </el-form-item>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <el-form-item label="库存" prop="stock">
            <el-input-number v-model="form.stock" :min="0" :precision="0" class="w-full" />
          </el-form-item>
          <el-form-item label="品牌" prop="brand">
            <el-input v-model="form.brand" placeholder="请输入品牌" />
          </el-form-item>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <el-form-item label="产地" prop="originCountry">
            <el-input v-model="form.originCountry" placeholder="请输入产地" />
          </el-form-item>
          <el-form-item label="规格" prop="specification">
            <el-input v-model="form.specification" placeholder="如：230ml/瓶" />
          </el-form-item>
        </div>

        <el-form-item label="商品简介" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入商品简介，用于列表展示"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="商品详情" prop="detail">
          <el-input 
            v-model="form.detail" 
            type="textarea" 
            :rows="10"
            placeholder="请输入商品详细描述，支持HTML格式"
          />
        </el-form-item>

        <el-form-item label="上架状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">立即上架</el-radio>
            <el-radio :label="0">放入仓库</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="submitting" @click="submitForm">
            {{ isEdit ? '保存修改' : '立即发布' }}
          </el-button>
          <el-button size="large" @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const formRef = ref(null)
const submitting = ref(false)

const form = ref({
  name: '',
  categoryId: [],
  image: '',
  detailImages: [],
  price: 0,
  originalPrice: 0,
  stock: 0,
  brand: '',
  originCountry: '',
  specification: '',
  description: '',
  detail: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  image: [{ required: true, message: '请上传商品图片', trigger: 'change' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const categoryOptions = ref([
  { id: 1, name: '美妆护肤', children: [{ id: 11, name: '面部护理' }, { id: 12, name: '彩妆' }] },
  { id: 2, name: '母婴用品', children: [{ id: 21, name: '奶粉' }, { id: 22, name: '纸尿裤' }] },
  { id: 3, name: '保健品', children: [{ id: 31, name: '维生素' }, { id: 32, name: '蛋白粉' }] },
])

const handleImageSuccess = (res) => {
  form.value.image = res.data
}

const handleDetailImageSuccess = (res, file) => {
  form.value.detailImages.push({
    name: file.name,
    url: res.data
  })
}

const handleDetailImageRemove = (file) => {
  const index = form.value.detailImages.findIndex(item => item.uid === file.uid)
  if (index > -1) {
    form.value.detailImages.splice(index, 1)
  }
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // 调用API保存商品
    ElMessage.success(isEdit.value ? '修改成功' : '发布成功')
    router.push('/merchant/products')
  } catch (error) {
    ElMessage.error(isEdit.value ? '修改失败' : '发布失败')
  } finally {
    submitting.value = false
  }
}

const fetchProduct = async () => {
  if (!isEdit.value) return
  try {
    // 调用API获取商品详情
    // form.value = res.data
  } catch (error) {
    ElMessage.error('获取商品详情失败')
  }
}

onMounted(fetchProduct)
</script>

<style scoped>
.avatar-uploader :deep(.el-upload) {
  border: none;
}
</style>