<!-- 注册页面 -->
<template>
  <div class="register-container">
    <div class="register-form">
      <div class="terminal-header">
        <span class="dot red"></span>
        <span class="dot yellow"></span>
        <span class="dot green"></span>
        <span class="terminal-title">auth::register</span>
      </div>
      <h2><span class="prompt">$</span> 注册</h2>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="用户身份" prop="identity">
          <el-input v-model="registerForm.identity" placeholder="请输入用户身份（学生/初级开发者等）" />
        </el-form-item>
        <el-form-item label="期望薪资" prop="salary">
          <el-input v-model.number="registerForm.salary" type="number" placeholder="请输入期望薪资/当前薪资（元）" />
        </el-form-item>
        <el-form-item label="工作经历" prop="experience">
          <el-input v-model="registerForm.experience" type="textarea" placeholder="请输入项目/工作经历" :rows="3" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="userStore.loading">注册</el-button>
          <el-button @click="goToLogin">登录</el-button>
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
const registerFormRef = ref(null)

const registerForm = reactive({
  username: '',
  password: '',
  identity: '',
  salary: null,
  experience: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 1, max: 50, message: '用户名长度在1-50位之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  identity: [
    { max: 23, message: '用户身份长度不超过23位', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await userStore.register(registerForm)
        if (response.code === 200) {
          ElMessage.success('注册成功')
          // 如果返回了token，自动登录
          if (response.data && response.data.token) {
            userStore.token = response.data.token
            userStore.isLoggedIn = true
            localStorage.setItem('token', response.data.token)
            router.push('/home')
          } else {
            // 否则跳转到登录页
            router.push('/login')
          }
        } else {
          ElMessage.error(response.msg)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.msg || '注册失败，请稍后重试')
      }
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  position: relative;
  animation: terminal-fade-in 0.6s ease-out;
}

/* 装饰光斑 */
.register-container::before {
  content: '';
  position: fixed;
  top: 15%;
  right: 10%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.04) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
  animation: corner-pulse 9s ease-in-out infinite;
  z-index: 0;
}

.register-container::after {
  content: '';
  position: fixed;
  bottom: 10%;
  left: 10%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(180, 74, 255, 0.04) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
  animation: corner-pulse 11s ease-in-out infinite reverse;
  z-index: 0;
}

.register-form {
  width: 470px;
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
  max-height: 90vh;
  overflow-y: auto;
}

/* 终端标题栏 */
.terminal-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(0, 212, 255, 0.05);
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

.register-form h2 {
  text-align: center;
  margin: 24px 0 20px;
  font-size: 24px;
  font-weight: 800;
  color: var(--neon-green);
  text-shadow: var(--glow-text-green);
  letter-spacing: 2px;
}

.register-form h2 .prompt {
  color: var(--neon-cyan);
  margin-right: 4px;
  animation: cursor-blink 1s step-end infinite;
}

.register-form :deep(.el-form) {
  padding: 0 36px 32px;
}

.el-form-item {
  margin-bottom: 16px;
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
</style>
