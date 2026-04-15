import request from './request'

export function getReviewList(productId, params) {
  return request({ url: `/review/product/${productId}`, method: 'get', params })
}

export function getProductReviews(productId, params) {
  return request({ url: `/review/product/${productId}`, method: 'get', params })
}

export function submitReview(data) {
  return request({ url: '/review/submit', method: 'post', data })
}
