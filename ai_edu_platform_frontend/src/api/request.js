import axios from 'axios'
import { ElMessage } from "element-plus";

const request = axios.create({
  baseURL: '/api',
  timeout: 60000 // AI生成题目可能需要较长时间，设置为60秒
})

// 是否正在刷新token的标志
let isRefreshing = false
// 重试队列，存储等待Token刷新的请求
let retryQueue = []

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
  async error => {
    if (error.response) {
      const { status, config } = error.response
      
      switch (status) {
        case 401:
          // Token 无效、过期或签名错误
          console.log('🔑 收到401响应，尝试刷新Token')
          
          // 如果当前请求是刷新Token的请求，直接跳转登录
          if (config.url === '/user/refresh-token') {
            localStorage.removeItem('token')
            localStorage.removeItem('refreshToken')
            localStorage.removeItem('userInfo')
            ElMessage.error('登录已过期，请重新登录')
            window.location.href = '/login'
            break
          }
          
          // 如果正在刷新Token，将请求加入队列
          if (isRefreshing) {
            console.log('⏳ Token正在刷新中，将请求加入队列')
            return new Promise((resolve) => {
              retryQueue.push(() => {
                resolve(request(config))
              })
            })
          }
          
          // 开始刷新Token
          isRefreshing = true
          const refreshToken = localStorage.getItem('refreshToken')
          
          if (!refreshToken) {
            console.warn('⚠️ 没有Refresh Token，直接跳转登录')
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            ElMessage.error('登录已过期，请重新登录')
            window.location.href = '/login'
            break
          }
          
          try {
            console.log('🔄 开始刷新Token...')
            // 调用刷新Token接口
            const refreshResponse = await axios.post('/api/user/refresh-token', {
              refreshToken
            })
            
            if (refreshResponse.data.code === 200) {
              const { token, refreshToken: newRefreshToken } = refreshResponse.data.data
              
              // 更新localStorage中的token
              localStorage.setItem('token', token)
              localStorage.setItem('refreshToken', newRefreshToken)
              
              console.log('✅ Token刷新成功')
              
              // 重试队列中的所有请求
              retryQueue.forEach(callback => callback())
              retryQueue = []
              
              // 重试当前请求
              config.headers.Authorization = `Bearer ${token}`
              return request(config)
            } else {
              throw new Error('Token刷新失败')
            }
          } catch (refreshError) {
            console.error('❌ Token刷新失败:', refreshError)
            // 刷新失败，清除登录状态并跳转登录
            localStorage.removeItem('token')
            localStorage.removeItem('refreshToken')
            localStorage.removeItem('userInfo')
            ElMessage.error('登录已过期，请重新登录')
            window.location.href = '/login'
          } finally {
            isRefreshing = false
          }
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