import request from './request'

export function toggleFavorite(productId) {
  return request({ url: `/favorite/toggle/${productId}`, method: 'post' })
}

export function addFavorite(data) {
  return request({ url: '/favorite/add', method: 'post', data })
}

export function checkFavorite(productId) {
  return request({ url: `/favorite/check/${productId}`, method: 'get' })
}

export function getFavoriteList() {
  return request({ url: '/favorite/list', method: 'get' })
}

export function removeFavorite(productId) {
  return request({ url: `/favorite/remove/${productId}`, method: 'delete' })
}
