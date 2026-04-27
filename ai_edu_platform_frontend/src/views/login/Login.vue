<!-- 登录页面 - Cyberpunk 2.0 -->
<template>
  <div class="login-container">
    <CodeRain :zIndex="0" :fontSize="12" :speed="0.6" :density="0.5" />
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
          <el-button type="primary" @click="handleLogin" :loading="userStore.loading" class="btn-ripple-wrap">登录</el-button>
          <el-button @click="goToRegister" class="btn-ripple-wrap">注册</el-button>
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
import CodeRain from '../../components/common/CodeRain.vue'

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
        if (response.code === 200) {
          ElMessage.success('登录成功')
          await userStore.fetchUserInfo()
          const userInfo = userStore.userInfo
          if (userInfo) {
            localStorage.setItem('userInfo', JSON.stringify(userInfo))
          }
          router.push('/home')
        } else {
          ElMessage.error(response.msg || '登录失败')
        }
      } catch (error) {
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
  animation: terminal-fade-in 0.8s ease-out;
}

.login-container::before {
  content: '';
  position: fixed;
  top: 15%;
  right: 10%;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.06) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
  animation: corner-pulse 8s ease-in-out infinite;
  z-index: 0;
}

.login-container::after {
  content: '';
  position: fixed;
  bottom: 10%;
  left: 10%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(180, 74, 255, 0.06) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
  animation: corner-pulse 10s ease-in-out infinite reverse;
  z-index: 0;
}

.login-form {
  width: 440px;
  padding: 0;
  background: var(--panel-bg-strong);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-lg);
  box-shadow: 0 0 40px rgba(0, 212, 255, 0.08), 0 0 80px rgba(180, 74, 255, 0.04);
  position: relative;
  z-index: 1;
  overflow: hidden;
  animation: breathe 4s ease-in-out infinite;
}

/* 终端标题栏 */
.terminal-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 18px;
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.06), rgba(180, 74, 255, 0.06));
  border-bottom: 1px solid var(--panel-border);
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
  transition: transform 0.2s ease;
}
.dot:hover { transform: scale(1.3); }
.dot.red { background: #ff5f56; box-shadow: 0 0 8px rgba(255, 95, 86, 0.6); }
.dot.yellow { background: #ffbd2e; box-shadow: 0 0 8px rgba(255, 189, 46, 0.6); }
.dot.green { background: #27c93f; box-shadow: 0 0 8px rgba(39, 201, 63, 0.6); }

.terminal-title {
  margin-left: 8px;
  font-size: 12px;
  color: var(--text-muted);
  letter-spacing: 1px;
}

.login-form h2 {
  text-align: center;
  margin: 28px 0 24px;
  font-size: 26px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 3px;
}

.login-form h2 .prompt {
  -webkit-text-fill-color: var(--neon-cyan);
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
}
</style>
