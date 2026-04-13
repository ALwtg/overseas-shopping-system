import request from './request'

export function getLogistics(orderNo) {
  return request({ url: `/logistics/order/${orderNo}`, method: 'get' })
}

export function addLogisticsNode(data) {
  return request({ url: '/logistics/add', method: 'post', data })
}
