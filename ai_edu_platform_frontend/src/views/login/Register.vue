<!-- 注册页面 - Cyberpunk 2.0 -->
<template>
  <div class="register-container">
    <CodeRain :zIndex="0" :fontSize="12" :speed="0.5" :density="0.5" />
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
          <el-button type="primary" @click="handleRegister" :loading="userStore.loading" class="btn-ripple-wrap">注册</el-button>
          <el-button @click="goToLogin" class="btn-ripple-wrap">登录</el-button>
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
          if (response.data && response.data.token) {
            userStore.token = response.data.token
            userStore.isLoggedIn = true
            localStorage.setItem('token', response.data.token)
            router.push('/home')
          } else {
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
  animation: terminal-fade-in 0.8s ease-out;
}

.register-container::before {
  content: '';
  position: fixed;
  top: 15%;
  right: 10%;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.06) 0%, transparent 70%);
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
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(180, 74, 255, 0.06) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
  animation: corner-pulse 11s ease-in-out infinite reverse;
  z-index: 0;
}

.register-form {
  width: 470px;
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
  max-height: 90vh;
  overflow-y: auto;
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
  position: sticky;
  top: 0;
  z-index: 2;
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

.register-form h2 {
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

.register-form h2 .prompt {
  -webkit-text-fill-color: var(--neon-cyan);
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
