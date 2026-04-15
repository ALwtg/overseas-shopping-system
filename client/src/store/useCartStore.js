import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCartList, addToCart, updateCartQty, removeCartItem } from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  const cartItems = ref([])
  const loading = ref(false)
  const cartCount = ref(0)

  const totalItems = computed(() => cartItems.value.reduce((sum, item) => sum + item.quantity, 0))
  const totalPrice = computed(() =>
    cartItems.value
      .filter(item => item.selected)
      .reduce((sum, item) => sum + (item.product?.price || 0) * item.quantity, 0)
  )

  async function fetchCart() {
    loading.value = true
    try {
      const res = await getCartList()
      cartItems.value = res.data || []
      cartCount.value = totalItems.value
    } catch (e) {}
    loading.value = false
  }

  async function fetchCartCount() {
    try {
      const res = await getCartList()
      const items = res.data || []
      cartCount.value = items.reduce((sum, item) => sum + item.quantity, 0)
    } catch (e) {}
  }

  async function addProduct(productId, quantity) {
    await addToCart({ productId, quantity: quantity || 1 })
    await fetchCart()
  }

  async function updateQuantity(id, quantity) {
    await updateCartQty(id, quantity)
    await fetchCart()
  }

  async function removeItem(id) {
    await removeCartItem(id)
    await fetchCart()
  }

  return { cartItems, loading, totalItems, totalPrice, cartCount, fetchCart, fetchCartCount, addProduct, updateQuantity, removeItem }
})
