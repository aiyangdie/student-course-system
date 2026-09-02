import axios from 'axios'
import { Message } from 'element-ui'
import router from '../router'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

let redirecting = false

function toLogin(message) {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('realName')
  if (!redirecting && router.currentRoute.path !== '/login') {
    redirecting = true
    Message.error(message || '登录已过期，请重新登录')
    router.replace('/login').finally(() => {
      redirecting = false
    })
  }
}

service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

service.interceptors.response.use(
  res => {
    const data = res.data
    if (data.code === 200) {
      return data
    }
    if (data.code === 401) {
      toLogin(data.message)
      // 阻断后续 then/catch，避免开发态 Uncaught runtime errors 红屏
      return new Promise(() => {})
    }
    Message.error(data.message || '请求失败')
    return Promise.reject(data)
  },
  err => {
    if (err.response && err.response.status === 401) {
      toLogin('登录已过期，请重新登录')
      return new Promise(() => {})
    }
    const msg = (err.response && err.response.data && err.response.data.message)
      || err.message
      || '网络错误'
    Message.error(msg)
    return Promise.reject(err)
  }
)

export default service
