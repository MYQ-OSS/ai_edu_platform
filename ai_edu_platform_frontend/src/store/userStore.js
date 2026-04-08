import { defineStore } from 'pinia'
import * as userApi from '../api/userApi'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    token: localStorage.getItem('token') || null,
    isLoggedIn: !!localStorage.getItem('token'),
    loading: false,
    error: null
  }),
  getters: {
    getUserInfo: (state) => state.userInfo,
    getToken: (state) => state.token,
    getIsLoggedIn: (state) => state.isLoggedIn,
    getLoading: (state) => state.loading,
    getError: (state) => state.error
  },
  actions: {
    // 注册
    async register(data) {
      this.loading = true
      this.error = null
      try {
        const response = await userApi.register(data)
        this.loading = false
        return response
      } catch (error) {
        this.error = error.message
        this.loading = false
        throw error
      }
    },
    // 登录
    async login(data) {
      this.loading = true
      this.error = null
      try {
        const response = await userApi.login(data)
        if (response.code === 200) {
          this.token = response.data.token
          this.isLoggedIn = true
          localStorage.setItem('token', response.data.token)
        }
        this.loading = false
        return response
      } catch (error) {
        this.error = error.message
        this.loading = false
        throw error
      }
    },
    // 获取个人信息
    async fetchUserInfo() {
      if (!this.token) return
      this.loading = true
      this.error = null
      try {
        const response = await userApi.getUserInfo()
        if (response.code === 200) {
          this.userInfo = response.data
        }
        this.loading = false
        return response
      } catch (error) {
        this.error = error.message
        this.loading = false
        throw error
      }
    },
    // 编辑个人信息
    async editUserInfo(data) {
      this.loading = true
      this.error = null
      try {
        const response = await userApi.editUserInfo(data)
        if (response.code === 200) {
          // 更新本地用户信息
          if (data.identity) this.userInfo.identity = data.identity
          if (data.salary) this.userInfo.salary = data.salary
          if (data.experience) this.userInfo.experience = data.experience
        }
        this.loading = false
        return response
      } catch (error) {
        this.error = error.message
        this.loading = false
        throw error
      }
    },
    // 获取学习足迹
    async getLearningHistory() {
      this.loading = true
      this.error = null
      try {
        const response = await userApi.getLearningHistory()
        this.loading = false
        return response
      } catch (error) {
        this.error = error.message
        this.loading = false
        throw error
      }
    },
    // 登出
    logout() {
      this.userInfo = null
      this.token = null
      this.isLoggedIn = false
      localStorage.removeItem('token')
    }
  }
})