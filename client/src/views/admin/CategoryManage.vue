<template>
  <div>
    <h2 class="text-2xl font-bold mb-6">分类管理</h2>

    <!-- 操作栏 -->
    <el-card class="mb-6">
      <el-button type="primary" @click="showAddDialog">
        <el-icon class="mr-1"><Plus /></el-icon>
        添加分类
      </el-button>
    </el-card>

    <!-- 分类列表 -->
    <el-card>
      <el-table :data="categories" row-key="id" :tree-props="{ children: 'children' }" stripe>
        <el-table-column label="分类名称" prop="name" min-width="200">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <span class="text-xl">{{ row.icon }}</span>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="图标" prop="icon" width="100" />
        <el-table-column label="排序" prop="sort" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="editCategory(row)">编辑</el-button>
            <el-button link type="success" @click="addSubCategory(row)">添加子类</el-button>
            <el-button link type="danger" @click="deleteCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分类表单弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '添加分类'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="父级分类">
          <el-cascader 
            v-model="form.parentId" 
            :options="categoryOptions"
            :props="{ checkStrictly: true, value: 'id', label: 'name', emitPath: false }"
            placeholder="请选择父级分类（不选则为顶级）"
            class="w-full"
            clearable
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入emoji图标，如：💄" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" class="w-full" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const categories = ref([])
const loading = ref(false)

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = ref({
  id: null,
  parentId: 0,
  name: '',
  icon: '',
  sort: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  icon: [{ required: true, message: '请输入图标', trigger: 'blur' }]
}

const categoryOptions = computed(() => [
  { id: 0, name: '顶级分类' },
  ...categories.value
])

const fetchCategories = async () => {
  loading.value = true
  try {
    categories.value = [
      {
        id: 1,
        name: '美妆护肤',
        icon: '💄',
        sort: 1,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        children: [
          { id: 11, name: '面部护理', icon: '✨', sort: 1, status: 1, createTime: '2024-01-01 10:00:00' },
          { id: 12, name: '彩妆', icon: '💋', sort: 2, status: 1, createTime: '2024-01-01 10:00:00' }
        ]
      },
      {
        id: 2,
        name: '母婴用品',
        icon: '🍼',
        sort: 2,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        children: [
          { id: 21, name: '奶粉', icon: '🥛', sort: 1, status: 1, createTime: '2024-01-01 10:00:00' },
          { id: 22, name: '纸尿裤', icon: '👶', sort: 2, status: 1, createTime: '2024-01-01 10:00:00' }
        ]
      }
    ]
  } catch (error) {
    ElMessage.error('获取分类失败')
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  form.value = { id: null, parentId: 0, name: '', icon: '', sort: 0, status: 1 }
  dialogVisible.value = true
}

const addSubCategory = (row) => {
  form.value = { id: null, parentId: row.id, name: '', icon: '', sort: 0, status: 1 }
  dialogVisible.value = true
}

const editCategory = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    ElMessage.success(form.value.id ? '修改成功' : '添加成功')
    dialogVisible.value = false
    fetchCategories()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const deleteCategory = async (row) => {
  if (row.children && row.children.length > 0) {
    ElMessage.warning('请先删除子分类')
    return
  }
  try {
    await ElMessageBox.confirm('确定删除该分类吗？', '提示', { type: 'warning' })
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(fetchCategories)
</script>