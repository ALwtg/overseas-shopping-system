import request from './request'

export function getUserInfo() {
  return request({ url: '/user/info', method: 'get' })
}

export function updateUserInfo(data) {
  return request({ url: '/user/info', method: 'put', data })
}

export function changePassword(data) {
  return request({ url: '/user/password', method: 'put', data })
}

export function getAddressList() {
  return request({ url: '/user/address', method: 'get' })
}

export function addAddress(data) {
  return request({ url: '/user/address', method: 'post', data })
}

export function updateAddress(data) {
  return request({ url: `/user/address/${data.id}`, method: 'put', data })
}

export function deleteAddress(id) {
  return request({ url: `/user/address/${id}`, method: 'delete' })
}
