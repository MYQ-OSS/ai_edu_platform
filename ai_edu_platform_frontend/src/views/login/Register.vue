<!-- 注册页面 -->
<template>
  <div class="register-container">
    <div class="register-form">
      <h2>注册</h2>
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
  background-color: #f5f5f5;
}

.register-form {
  width: 450px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-form h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}

.el-form-item {
  margin-bottom: 15px;
}

.el-form-item__content {
  display: flex;
  justify-content: space-between;
}

.el-button {
  width: 48%;
}
</style>