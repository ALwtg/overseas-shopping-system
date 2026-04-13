import request from './request'

export function getUserInfo() {
  return request({ url: '/user/info', method: 'get' })
}

export function updateUserInfo(data) {
  return request({ url: '/user/info', method: 'put', data })
}
