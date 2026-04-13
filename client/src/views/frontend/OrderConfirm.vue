<template>
  <div class="min-h-screen bg-gray-50">
    <AppHeader />
    
    <div class="container mx-auto px-4 py-8">
      <h1 class="text-2xl font-bold mb-6">确认订单</h1>

      <div v-if="loading" class="text-center py-20">
        <el-skeleton :rows="10" animated />
      </div>

      <template v-else>
        <!-- 收货地址 -->
        <div class="bg-white rounded-lg shadow-md p-6 mb-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-medium">收货地址</h2>
            <el-button link type="primary" @click="showAddressDialog = true">
              管理地址
            </el-button>
          </div>
          
          <div v-if="addresses.length === 0" class="text-center py-6 text-gray-500">
            暂无收货地址，<el-button link type="primary" @click="showAddAddress">添加地址</el-button>
          </div>
          
          <el-radio-group v-else v-model="selectedAddressId" class="w-full">
            <div class="grid md:grid-cols-2 gap-4">
              <div 
                v-for="addr in addresses" 
                :key="addr.id"
                class="border rounded-lg p-4 cursor-pointer transition-all"
                :class="selectedAddressId === addr.id ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-gray-300'"
                @click="selectedAddressId = addr.id"
              >
                <div class="flex items-start gap-3">
                  <el-radio :label="addr.id">&nbsp;</el-radio>
                  <div class="flex-1">
                    <div class="flex items-center gap-2 mb-2">
                      <span class="font-medium">{{ addr.receiverName }}</span>
                      <span class="text-gray-600">{{ addr.receiverPhone }}</span>
                      <el-tag v-if="addr.isDefault" type="success" size="small">默认</el-tag>
                    </div>
                    <p class="text-gray-600 text-sm">
                      {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </el-radio-group>
        </div>

        <!-- 商品清单 -->
        <div class="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 class="text-lg font-medium mb-4">商品清单</h2>
          <el-table :data="orderItems" :show-header="false">
            <el-table-column min-width="400">
              <template #default="{ row }">
                <div class="flex items-center gap-4">
                  <img :src="row.image" class="w-16 h-16 object-cover rounded" :alt="row.name">
                  <div>
                    <div class="font-medium">{{ row.name }}</div>
                    <div class="text-sm text-gray-500">{{ row.specification }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column width="120" align="center">
              <template #default="{ row }">
                <span class="text-red-600">¥{{ row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column width="100" align="center">
              <template #default="{ row }">
                x{{ row.quantity }}
              </template>
            </el-table-column>
            <el-table-column width="120" align="right">
              <template #default="{ row }">
                <span class="text-red-600 font-medium">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 配送方式 -->
        <div class="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 class="text-lg font-medium mb-4">配送方式</h2>
          <el-radio-group v-model="shippingMethod">
            <el-radio label="standard">标准快递（5-10个工作日）</el-radio>
            <el-radio label="express">快速物流（3-5个工作日）+¥30</el-radio>
          </el-radio-group>
        </div>

        <!-- 订单备注 -->
        <div class="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 class="text-lg font-medium mb-4">订单备注</h2>
          <el-input 
            v-model="remark" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入订单备注（选填）"
            maxlength="200"
            show-word-limit
          />
        </div>

        <!-- 结算信息 -->
        <div class="bg-white rounded-lg shadow-md p-6">
          <div class="flex justify-end items-center gap-8">
            <div class="text-right">
              <div class="mb-2">
                <span class="text-gray-600">商品总价：</span>
                <span class="text-lg">¥{{ productTotal.toFixed(2) }}</span>
              </div>
              <div class="mb-2">
                <span class="text-gray-600">运费：</span>
                <span class="text-lg">¥{{ shippingFee.toFixed(2) }}</span>
              </div>
              <div>
                <span class="text-gray-600">应付总额：</span>
                <span class="text-3xl font-bold text-red-600">¥{{ finalTotal.toFixed(2) }}</span>
              </div>
            </div>
            <el-button 
              type="danger" 
              size="large"
              :disabled="!selectedAddressId || submitting"
              :loading="submitting"
              @click="submitOrder"
            >
              提交订单
            </el-button>
          </div>
        </div>
      </template>
    </div>

    <!-- 地址管理弹窗 -->
    <el-dialog v-model="showAddressDialog" title="收货地址" width="800px">
      <el-button type="primary" class="mb-4" @click="showAddAddress">添加新地址</el-button>
      <el-table :data="addresses">
        <el-table-column label="收货人" prop="receiverName" width="100" />
        <el-table-column label="手机号" prop="receiverPhone" width="120" />
        <el-table-column label="地址">
          <template #default="{ row }">
            {{ row.province }} {{ row.city }} {{ row.district }} {{ row.detailAddress }}
          </template>
        </el-table-column>
        <el-table-column label="默认" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault" type="success" size="small">是</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="editAddress(row)">编辑</el-button>
            <el-button link type="danger" @click="deleteAddress(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加/编辑地址弹窗 -->
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
          <el-input 
            v-model="addressForm.detailAddress" 
            type="textarea" 
            :rows="2"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddressForm = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { getAddressList, addAddress, updateAddress, deleteAddress as deleteAddressApi } from '@/api/user'
import { createOrder } from '@/api/order'
import { getProductDetail } from '@/api/product'
import { getCartList } from '@/api/cart'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
const orderItems = ref([])
const addresses = ref([])
const selectedAddressId = ref(null)
const shippingMethod = ref('standard')
const remark = ref('')
const showAddressDialog = ref(false)
const showAddressForm = ref(false)
const addressForm = ref({
  receiverName: '',
  receiverPhone: '',
  region: [],
  detailAddress: '',
  isDefault: false
})

const regionOptions = [
  { value: '北京市', label: '北京市', children: [{ value: '北京市', label: '北京市', children: [{ value: '朝阳区', label: '朝阳区' }, { value: '海淀区', label: '海淀区' }] }] },
  { value: '上海市', label: '上海市', children: [{ value: '上海市', label: '上海市', children: [{ value: '浦东新区', label: '浦东新区' }, { value: '黄浦区', label: '黄浦区' }] }] },
  { value: '广东省', label: '广东省', children: [{ value: '广州市', label: '广州市', children: [{ value: '天河区', label: '天河区' }] }, { value: '深圳市', label: '深圳市', children: [{ value: '南山区', label: '南山区' }] }] },
]

const productTotal = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const shippingFee = computed(() => {
  return shippingMethod.value === 'express' ? 30 : (productTotal.value >= 299 ? 0 : 15)
})

const finalTotal = computed(() => {
  return productTotal.value + shippingFee.value
})

const initOrderData = async () => {
  loading.value = true
  try {
    const { productId, quantity, cartItemIds } = route.query
    
    if (productId) {
      // 立即购买
      const res = await getProductDetail(productId)
      orderItems.value = [{
        productId: res.data.id,
        name: res.data.name,
        image: res.data.image,
        price: res.data.price,
        quantity: parseInt(quantity) || 1,
        specification: res.data.specification
      }]
    } else if (cartItemIds) {
      // 购物车结算
      const ids = cartItemIds.split(',').map(Number)
      const res = await getCartList()
      const cartItems = res.data.filter(item => ids.includes(item.id))
      orderItems.value = cartItems.map(item => ({
        cartItemId: item.id,
        productId: item.productId,
        name: item.productName,
        image: item.productImage,
        price: item.productPrice,
        quantity: item.quantity,
        specification: item.productSpecification
      }))
    }
    
    // 获取地址列表
    const addrRes = await getAddressList()
    addresses.value = addrRes.data || []
    const defaultAddr = addresses.value.find(a => a.isDefault)
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.id
    } else if (addresses.value.length > 0) {
      selectedAddressId.value = addresses.value[0].id
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
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

const editAddress = (row) => {
  addressForm.value = { 
    ...row,
    region: [row.province, row.city, row.district]
  }
  showAddressForm.value = true
}

const saveAddress = async () => {
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
    showAddressDialog.value = false
    const res = await getAddressList()
    addresses.value = res.data
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const deleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该地址吗？', '提示', { type: 'warning' })
    await deleteAddressApi(id)
    ElMessage.success('删除成功')
    const res = await getAddressList()
    addresses.value = res.data
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const submitOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  
  submitting.value = true
  try {
    const address = addresses.value.find(a => a.id === selectedAddressId.value)
    const data = {
      addressId: selectedAddressId.value,
      receiverName: address.receiverName,
      receiverPhone: address.receiverPhone,
      address: `${address.province}${address.city}${address.district}${address.detailAddress}`,
      remark: remark.value,
      items: orderItems.value.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        price: item.price
      }))
    }
    const res = await createOrder(data)
    ElMessage.success('订单提交成功')
    router.push(`/order/detail/${res.data}`)
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(initOrderData)
</script>