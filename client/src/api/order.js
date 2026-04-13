import request from './request'

export function getOrderList(params) {
  return request({ url: '/order/list', method: 'get', params })
}

export function createOrder(data) {
  return request({ url: '/order/create', method: 'post', data })
}

export function getOrderDetail(id) {
  return request({ url: `/order/${id}`, method: 'get' })
}
