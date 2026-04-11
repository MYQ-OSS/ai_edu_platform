import axios from 'axios'
import { ElMessage } from "element-plus";

const request = axios.create({
  baseURL: '/api',
  timeout: 60000 // AI生成题目可能需要较长时间，设置为60秒
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    console.log('🔑 请求拦截器 - Token:', token ? `${token.substring(0, 20)}...` : '无')
    console.log('🔑 请求拦截器 - URL:', config.url)
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('✅ 已添加 Authorization header')
    } else {
      console.warn('⚠️ 未找到 Token，请求将不带认证信息')
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // Token 无效、过期或签名错误，清除本地存储并跳转登录
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          ElMessage.error('登录已过期，请重新登录')
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error('请求失败')
      }
    }
    return Promise.reject(error)
  }
)

export default request