import request from './request'

export function getAnnouncementList() {
  return request({ url: '/announcement/list', method: 'get' })
}

export function getAllAnnouncements() {
  return request({ url: '/announcement/all', method: 'get' })
}
