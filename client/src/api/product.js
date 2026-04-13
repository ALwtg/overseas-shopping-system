import request from './request'

export function getProductList(params) {
  return request({ url: '/product/list', method: 'get', params })
}

export function getProductDetail(id) {
  return request({ url: `/product/${id}`, method: 'get' })
}

export function getHotProducts() {
  return request({ url: '/product/hot', method: 'get' })
}
