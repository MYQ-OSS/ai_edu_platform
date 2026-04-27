<!-- 个人信息编辑页面 -->
<template>
  <div class="edit-info-container">
    <h2>编辑个人信息</h2>
    
    <div class="edit-card">
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="120px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" placeholder="请输入用户名" maxlength="50" show-word-limit />
        </el-form-item>
        
        <el-form-item label="用户身份" prop="identity">
          <el-input v-model="editForm.identity" placeholder="请输入用户身份（如：学生）" maxlength="23" show-word-limit />
        </el-form-item>
        
        <el-form-item label="期望薪资" prop="salary">
          <el-input v-model.number="editForm.salary" type="number" placeholder="请输入期望薪资（元）" />
        </el-form-item>
        
        <el-form-item label="工作经历" prop="experience">
          <el-input 
            v-model="editForm.experience" 
            type="textarea" 
            placeholder="请输入项目/工作经历" 
            :rows="5"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="修改密码" prop="password">
          <el-input 
            v-model="editForm.password" 
            type="password" 
            placeholder="如需修改密码请输入新密码，否则留空" 
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="editForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码" 
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
          <el-button @click="goBack">取消</el-button>
          <el-button @click="goHome">返回首页</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store/userStore'

const router = useRouter()
const userStore = useUserStore()
const editFormRef = ref(null)
const loading = ref(false)

const editForm = reactive({
  username: '',
  identity: '',
  salary: null,
  experience: '',
  password: '',
  confirmPassword: ''
})

// 自定义密码验证规则
const validatePassword = (rule, value, callback) => {
  if (value && value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (editForm.password && !value) {
    callback(new Error('请再次输入密码'))
  } else if (value && value !== editForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { max: 50, message: '用户名长度不超过50位', trigger: 'blur' }
  ],
  identity: [
    { max: 23, message: '用户身份长度不超过23位', trigger: 'blur' }
  ],
  salary: [
    { type: 'number', message: '请输入有效的薪资', trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 页面加载时获取当前用户信息
onMounted(async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    const response = await userStore.fetchUserInfo()
    if (response.code === 200) {
      const userInfo = response.data
      editForm.username = userInfo.username
      editForm.identity = userInfo.identity || ''
      editForm.salary = userInfo.salary
      editForm.experience = userInfo.experience || ''
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
})

const handleSubmit = async () => {
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 构造提交数据
        const submitData = {
          username: editForm.username,
          identity: editForm.identity,
          salary: editForm.salary,
          experience: editForm.experience
        }
        
        // 只有当密码不为空时才添加密码字段
        if (editForm.password) {
          submitData.password = editForm.password
        }
        
        const response = await userStore.editUserInfo(submitData)
        if (response.code === 200) {
          ElMessage.success('个人信息更新成功')
          // 重新获取用户信息
          await userStore.fetchUserInfo()
          router.push('/personal/info')
        } else {
          ElMessage.error(response.msg)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.msg || '更新失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const goBack = () => {
  router.push('/personal/info')
}

const goHome = () => {
  router.push('/home')
}
</script>

<style scoped>
.edit-info-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  animation: terminal-fade-in 0.6s ease-out;
  position: relative;
}

.edit-info-container h2 {
  margin-bottom: 20px;
  text-align: center;
  font-size: 26px;
  font-weight: 800;
  color: var(--neon-green);
  text-shadow: var(--glow-text-green);
  letter-spacing: 2px;
  font-family: 'JetBrains Mono', monospace;
}

.edit-info-container h2::before {
  content: '> ';
  color: var(--neon-cyan);
  animation: cursor-blink 1s step-end infinite;
}

.edit-card {
  background: var(--panel-bg);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--panel-border);
  padding: 32px;
  border-radius: var(--radius-md);
  box-shadow: var(--panel-glow);
}

.el-form-item {
  margin-bottom: 18px;
}

.el-form-item :deep(.el-form-item__label) {
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
  font-size: 13px;
}

.el-form-item :deep(.el-input__wrapper) {
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid var(--panel-border);
  box-shadow: none;
  transition: border-color var(--transition-base), box-shadow var(--transition-base);
}

.el-form-item :deep(.el-input__wrapper:hover) {
  border-color: var(--neon-cyan);
}

.el-form-item :deep(.el-input__wrapper.is-focus) {
  border-color: var(--neon-green);
  box-shadow: var(--glow-green);
}

.el-form-item :deep(.el-input__inner) {
  color: var(--text-primary);
  font-family: 'JetBrains Mono', monospace;
}

.el-form-item :deep(.el-textarea__inner) {
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid var(--panel-border);
  color: var(--text-primary);
  font-family: 'JetBrains Mono', monospace;
}

.el-form-item :deep(.el-textarea__inner:focus) {
  border-color: var(--neon-green);
  box-shadow: var(--glow-green);
}

.el-button {
  margin-right: 10px;
  font-family: 'JetBrains Mono', monospace;
  font-weight: 700;
  letter-spacing: 1px;
}
</style>
