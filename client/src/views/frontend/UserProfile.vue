<template>
  <div class="min-h-screen bg-gray-50">
    <AppHeader />
    
    <div class="container mx-auto px-4 pt-28 pb-8">
      <div class="grid md:grid-cols-4 gap-6">
        <!-- 侧边导航 -->
        <div class="md:col-span-1">
          <div class="bg-white rounded-lg shadow-md overflow-hidden">
            <div class="p-6 text-center border-b">
              <el-avatar :size="80" :src="userInfo.avatar">
                {{ userInfo.username?.charAt(0) }}
              </el-avatar>
              <div class="mt-3 font-medium">{{ userInfo.username }}</div>
              <div class="text-sm text-gray-500">{{ userInfo.email }}</div>
            </div>
            <el-menu :default-active="activeMenu" class="border-0">
              <el-menu-item index="profile" @click="activeTab = 'profile'">
                <el-icon><User /></el-icon>
                <span>个人信息</span>
              </el-menu-item>
              <el-menu-item index="address" @click="activeTab = 'address'">
                <el-icon><Location /></el-icon>
                <span>收货地址</span>
              </el-menu-item>
              <el-menu-item index="favorite" @click="activeTab = 'favorite'">
                <el-icon><Star /></el-icon>
                <span>我的收藏</span>
              </el-menu-item>
              <el-menu-item index="security" @click="activeTab = 'security'">
                <el-icon><Lock /></el-icon>
                <span>账号安全</span>
              </el-menu-item>
            </el-menu>
          </div>
        </div>

        <!-- 主内容区 -->
        <div class="md:col-span-3">
          <!-- 个人信息 -->
          <div v-if="activeTab === 'profile'" class="bg-white rounded-lg shadow-md p-6">
            <h2 class="text-xl font-bold mb-6">个人信息</h2>
            <el-form :model="userForm" label-width="100px" class="max-w-lg">
              <el-form-item label="头像">
                <el-upload
                  class="avatar-uploader"
                  action="/api/upload"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                >
                  <img v-if="userForm.avatar" :src="userForm.avatar" class="w-24 h-24 rounded-full object-cover">
                  <el-icon v-else class="text-4xl text-gray-400"><Plus /></el-icon>
                </el-upload>
              </el-form-item>
              <el-form-item label="用户名">
                <el-input v-model="userForm.username" disabled />
              </el-form-item>
              <el-form-item label="昵称">
                <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
              <el-form-item label="真实姓名">
                <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="userForm.gender">
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="2">女</el-radio>
                  <el-radio :label="0">保密</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="生日">
                <el-date-picker v-model="userForm.birthday" type="date" placeholder="选择生日" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="saving" @click="saveProfile">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 收货地址 -->
          <div v-if="activeTab === 'address'" class="bg-white rounded-lg shadow-md p-6">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-xl font-bold">收货地址</h2>
              <el-button type="primary" @click="showAddAddress">添加地址</el-button>
            </div>
            
            <div v-if="addresses.length === 0" class="text-center py-10 text-gray-500">
              暂无收货地址
            </div>
            
            <div v-else class="space-y-4">
              <div 
                v-for="addr in addresses" 
                :key="addr.id"
                class="border rounded-lg p-4"
                :class="addr.isDefault ? 'border-blue-500 bg-blue-50' : ''"
              >
                <div class="flex items-start justify-between">
                  <div>
                    <div class="flex items-center gap-3 mb-2">
                      <span class="font-medium">{{ addr.receiverName }}</span>
                      <span class="text-gray-600">{{ addr.receiverPhone }}</span>
                      <el-tag v-if="addr.isDefault" type="success" size="small">默认</el-tag>
                    </div>
                    <p class="text-gray-600">
                      {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}
                    </p>
                  </div>
                  <div class="flex gap-2">
                    <el-button link type="primary" @click="editAddress(addr)">编辑</el-button>
                    <el-button link type="danger" @click="deleteAddress(addr.id)">删除</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 我的收藏 -->
          <div v-if="activeTab === 'favorite'" class="bg-white rounded-lg shadow-md p-6">
            <h2 class="text-xl font-bold mb-6">我的收藏</h2>
            
            <div v-if="favorites.length === 0" class="text-center py-10 text-gray-500">
              暂无收藏商品
            </div>
            
            <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
              <div 
                v-for="item in favorites" 
                :key="item.id"
                class="border rounded-lg overflow-hidden hover:shadow-lg transition-shadow cursor-pointer"
                @click="$router.push(`/product/${item.productId}`)"
              >
                <img :src="item.productImage" class="w-full aspect-square object-cover" :alt="item.productName">
                <div class="p-3">
                  <div class="text-sm font-medium truncate">{{ item.productName }}</div>
                  <div class="text-red-600 font-bold mt-1">¥{{ item.productPrice }}</div>
                  <el-button 
                    link 
                    type="danger" 
                    class="mt-2"
                    @click.stop="removeFavorite(item.productId)"
                  >
                    取消收藏
                  </el-button>
                </div>
              </div>
            </div>

            <div v-if="favorites.length > 0" class="flex justify-center mt-6">
              <el-pagination
                v-model:current-page="favoritePage"
                v-model:page-size="favoritePageSize"
                :total="favoriteTotal"
                layout="prev, pager, next"
                @change="fetchFavorites"
              />
            </div>
          </div>

          <!-- 账号安全 -->
          <div v-if="activeTab === 'security'" class="bg-white rounded-lg shadow-md p-6">
            <h2 class="text-xl font-bold mb-6">账号安全</h2>
            <div class="space-y-6">
              <div class="flex items-center justify-between py-4 border-b">
                <div>
                  <div class="font-medium">登录密码</div>
                  <div class="text-sm text-gray-500">定期修改密码可以保护账号安全</div>
                </div>
                <el-button @click="showPasswordDialog = true">修改密码</el-button>
              </div>
              <div class="flex items-center justify-between py-4 border-b">
                <div>
                  <div class="font-medium">手机绑定</div>
                  <div class="text-sm text-gray-500">已绑定：{{ userInfo.phone || '未绑定' }}</div>
                </div>
                <el-button>更换手机</el-button>
              </div>
              <div class="flex items-center justify-between py-4 border-b">
                <div>
                  <div class="font-medium">邮箱绑定</div>
                  <div class="text-sm text-gray-500">已绑定：{{ userInfo.email || '未绑定' }}</div>
                </div>
                <el-button>更换邮箱</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 地址表单弹窗 -->
    <el-dialog v-model="showAddressForm" :title="addressForm.id ? '编辑地址' : '添加地址'" width="500px">
      <el-form :model="addressForm" label-width="80px">
        <el-form-item label="收货人" required>
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" required>
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省市区" required>
          <el-cascader 
            v-model="addressForm.region" 
            :options="regionOptions"
            placeholder="请选择省市区"
            class="w-full"
          />
        </el-form-item>
        <el-form-item label="详细地址" required>
          <el-input v-model="addressForm.detailAddress" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddressForm = false">取消</el-button>
        <el-button type="primary" :loading="savingAddress" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="当前密码" required>
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
        </el-form-item>
        <el-form-item label="新密码" required>
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" required>
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" :loading="changingPassword" @click="changePassword">确认修改</el-button>
      </template>
    </el-dialog>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Location, Star, Lock, Plus } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { getUserInfo, updateUserInfo, changePassword as changePasswordApi } from '@/api/user'
import { getAddressList, addAddress, updateAddress, deleteAddress as deleteAddressApi } from '@/api/user'
import { getFavoriteList, removeFavorite as removeFavoriteApi } from '@/api/favorite'
import { useUserStore } from '@/store/useUserStore'

const userStore = useUserStore()

const activeTab = ref('profile')
const activeMenu = computed(() => activeTab.value)

const userInfo = ref({})
const userForm = ref({})
const saving = ref(false)

const addresses = ref([])
const showAddressForm = ref(false)
const savingAddress = ref(false)
const addressForm = ref({
  receiverName: '',
  receiverPhone: '',
  region: [],
  detailAddress: '',
  isDefault: false
})

const favorites = ref([])
const favoritePage = ref(1)
const favoritePageSize = ref(12)
const favoriteTotal = ref(0)

const showPasswordDialog = ref(false)
const changingPassword = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const regionOptions = [
  { value: '北京市', label: '北京市', children: [{ value: '北京市', label: '北京市', children: [{ value: '朝阳区', label: '朝阳区' }, { value: '海淀区', label: '海淀区' }] }] },
  { value: '上海市', label: '上海市', children: [{ value: '上海市', label: '上海市', children: [{ value: '浦东新区', label: '浦东新区' }, { value: '黄浦区', label: '黄浦区' }] }] },
  { value: '广东省', label: '广东省', children: [{ value: '广州市', label: '广州市', children: [{ value: '天河区', label: '天河区' }] }, { value: '深圳市', label: '深圳市', children: [{ value: '南山区', label: '南山区' }] }] },
]

const initData = async () => {
  try {
    const res = await getUserInfo()
    userInfo.value = res.data
    userForm.value = { ...res.data }
  } catch (error) {
    console.error(error)
  }
}

const saveProfile = async () => {
  saving.value = true
  try {
    console.log('[saveProfile] Calling updateUserInfo...')
    const res = await updateUserInfo(userForm.value)
    console.log('[saveProfile] Response:', res)
    userInfo.value = { ...userForm.value }
    userStore.setUserInfo(userInfo.value)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('[saveProfile] Error:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleAvatarSuccess = (res) => {
  userForm.value.avatar = res.data
}

const fetchAddresses = async () => {
  try {
    const res = await getAddressList()
    addresses.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const showAddAddress = () => {
  addressForm.value = {
    receiverName: '',
    receiverPhone: '',
    region: [],
    detailAddress: '',
    isDefault: false
  }
  showAddressForm.value = true
}

const editAddress = (addr) => {
  addressForm.value = {
    ...addr,
    region: [addr.province, addr.city, addr.district]
  }
  showAddressForm.value = true
}

const saveAddress = async () => {
  savingAddress.value = true
  try {
    const data = {
      ...addressForm.value,
      province: addressForm.value.region[0],
      city: addressForm.value.region[1],
      district: addressForm.value.region[2]
    }
    if (addressForm.value.id) {
      await updateAddress(addressForm.value.id, data)
    } else {
      await addAddress(data)
    }
    ElMessage.success('保存成功')
    showAddressForm.value = false
    fetchAddresses()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    savingAddress.value = false
  }
}

const deleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该地址吗？', '提示', { type: 'warning' })
    await deleteAddressApi(id)
    ElMessage.success('删除成功')
    fetchAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const fetchFavorites = async () => {
  try {
    const res = await getFavoriteList({
      page: favoritePage.value,
      pageSize: favoritePageSize.value
    })
    favorites.value = res.data.records || []
    favoriteTotal.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const removeFavorite = async (productId) => {
  try {
    await removeFavoriteApi(productId)
    ElMessage.success('取消收藏成功')
    fetchFavorites()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const changePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  changingPassword.value = true
  try {
    await changePasswordApi({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    ElMessage.success('密码修改成功')
    showPasswordDialog.value = false
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (error) {
    ElMessage.error('密码修改失败')
  } finally {
    changingPassword.value = false
  }
}

onMounted(() => {
  initData()
  fetchAddresses()
  fetchFavorites()
})
</script>

<style scoped>
.avatar-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}
</style>