<!-- 登录页面 -->
<template>
  <div class="login-container">
    <div class="login-form">
      <h2>登录</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="userStore.loading">登录</el-button>
          <el-button @click="goToRegister">注册</el-button>
        </el-form-item>
        <el-form-item>
          <div class="register-tip">
            还没有注册的请先注册哦
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store/userStore'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 1, max: 50, message: '用户名长度在1-50位之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await userStore.login(loginForm)
        console.log('登录响应:', response)
        
        if (response.code === 200) {
          ElMessage.success('登录成功')
          
          // 获取用户信息（包含 role）
          await userStore.fetchUserInfo()
          console.log('用户信息:', userStore.userInfo)
          
          // 将 role 信息存入 localStorage，供 isAdmin getter 使用
          const userInfo = userStore.userInfo
          if (userInfo) {
            localStorage.setItem('userInfo', JSON.stringify(userInfo))
            console.log('已保存 userInfo 到 localStorage:', userInfo)
          }
          
          router.push('/home')
        } else {
          ElMessage.error(response.msg || '登录失败')
        }
      } catch (error) {
        console.error('登录错误:', error)
        ElMessage.error(error.response?.data?.msg || '登录失败，请稍后重试')
      }
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-form {
  width: 400px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-form h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-form-item__content {
  display: flex;
  justify-content: space-between;
}

.el-button {
  width: 48%;
}

.register-tip {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-top: 10px;
}
</style>