import request from '../utils/request'

export function login(data) {
  return request.post('/auth/login', data)
}

export function logout() {
  return request.post('/auth/logout')
}

export function getInfo() {
  return request.get('/auth/info')
}

export function getStats() {
  return request.get('/dashboard/stats')
}

export function getOverview() {
  return request.get('/dashboard/overview')
}

export function pageApi(module, params) {
  return request.get(`/${module}/page`, { params })
}

export function listApi(module) {
  return request.get(`/${module}/list`)
}

export function getApi(module, id) {
  return request.get(`/${module}/${id}`)
}

export function addApi(module, data) {
  return request.post(`/${module}`, data)
}

export function updateApi(module, data) {
  return request.put(`/${module}`, data)
}

export function deleteApi(module, id) {
  return request.delete(`/${module}/${id}`)
}
