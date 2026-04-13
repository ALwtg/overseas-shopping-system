import request from './request'

export function getCartList() {
  return request({ url: '/cart/list', method: 'get' })
}

export function addToCart(data) {
  return request({ url: '/cart/add', method: 'post', data })
}

export function updateCartQty(id, quantity) {
  return request({ url: `/cart/${id}`, method: 'put', data: { quantity } })
}

export function removeCartItem(id) {
  return request({ url: `/cart/${id}`, method: 'delete' })
}
