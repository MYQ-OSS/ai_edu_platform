<!-- 登录页面 -->
<template>
  <div class="login-container">
    <div class="login-form">
      <div class="terminal-header">
        <span class="dot red"></span>
        <span class="dot yellow"></span>
        <span class="dot green"></span>
        <span class="terminal-title">auth::login</span>
      </div>
      <h2><span class="prompt">$</span> 登录</h2>
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
  position: relative;
  animation: terminal-fade-in 0.6s ease-out;
}

/* 装饰光斑 */
.login-container::before {
  content: '';
  position: fixed;
  top: 20%;
  left: 10%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(0, 255, 65, 0.04) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
  animation: corner-pulse 8s ease-in-out infinite;
  z-index: 0;
}

.login-container::after {
  content: '';
  position: fixed;
  bottom: 10%;
  right: 10%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.04) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
  animation: corner-pulse 10s ease-in-out infinite reverse;
  z-index: 0;
}

.login-form {
  width: 440px;
  padding: 0;
  background: var(--panel-bg);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-md);
  box-shadow: var(--panel-glow);
  position: relative;
  z-index: 1;
  overflow: hidden;
}

/* 终端标题栏 */
.terminal-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(0, 255, 65, 0.05);
  border-bottom: 1px solid var(--panel-border);
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}
.dot.red { background: #ff5f56; box-shadow: 0 0 6px rgba(255, 95, 86, 0.5); }
.dot.yellow { background: #ffbd2e; box-shadow: 0 0 6px rgba(255, 189, 46, 0.5); }
.dot.green { background: #27c93f; box-shadow: 0 0 6px rgba(39, 201, 63, 0.5); }

.terminal-title {
  margin-left: 8px;
  font-size: 12px;
  color: var(--text-muted);
  font-family: 'JetBrains Mono', monospace;
  letter-spacing: 1px;
}

.login-form h2 {
  text-align: center;
  margin: 24px 0 20px;
  font-size: 24px;
  font-weight: 800;
  color: var(--neon-green);
  text-shadow: var(--glow-text-green);
  letter-spacing: 2px;
}

.login-form h2 .prompt {
  color: var(--neon-cyan);
  margin-right: 4px;
  animation: cursor-blink 1s step-end infinite;
}

.login-form :deep(.el-form) {
  padding: 0 36px 32px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-form-item :deep(.el-form-item__label) {
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
  font-size: 13px;
}

.el-form-item__content {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.el-button {
  width: 48%;
}

.register-tip {
  text-align: center;
  color: var(--text-muted);
  font-size: 13px;
  margin-top: 8px;
  font-family: 'JetBrains Mono', monospace;
}
</style>
