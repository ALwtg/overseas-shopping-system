import request from './request'

export function getCategoryTree() {
  return request({ url: '/category/tree', method: 'get' })
}

export function getCategoryList() {
  return request({ url: '/category/list', method: 'get' })
}
