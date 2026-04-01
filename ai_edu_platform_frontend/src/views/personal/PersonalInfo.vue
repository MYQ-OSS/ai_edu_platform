<!-- 个人信息页面 -->
<template>
  <div class="personal-info-container">
    <h2>个人中心 - 个人信息</h2>
    <div class="info-card">
      <el-form :model="userForm" :rules="rules" ref="userFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" disabled />
        </el-form-item>
        <el-form-item label="用户身份" prop="identity">
          <el-input v-model="userForm.identity" placeholder="请输入用户身份" />
        </el-form-item>
        <el-form-item label="期望薪资" prop="salary">
          <el-input v-model.number="userForm.salary" type="number" placeholder="请输入期望薪资/当前薪资（元）" />
        </el-form-item>
        <el-form-item label="工作经历" prop="experience">
          <el-input v-model="userForm.experience" type="textarea" placeholder="请输入项目/工作经历" :rows="4" />
        </el-form-item>
        <el-form-item label="答题次数">
          <el-input v-model="userForm.answer_times" disabled />
        </el-form-item>
        <el-form-item label="平均得分">
          <el-input v-model="userForm.average_score" disabled />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="userStore.loading">保存修改</el-button>
          <el-button @click="handleLogout">退出登录</el-button>
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
const userFormRef = ref(null)

const userForm = reactive({
  id: '',
  username: '',
  identity: '',
  salary: '',
  experience: '',
  answer_times: '',
  average_score: ''
})

const rules = {
  identity: [
    { max: 23, message: '用户身份长度不超过23位', trigger: 'blur' }
  ]
}

// 页面加载时获取个人信息
onMounted(async () => {
  await loadUserInfo()
})

const loadUserInfo = async () => {
  try {
    const response = await userStore.getUserInfo()
    if (response.code === 200) {
      Object.assign(userForm, response.data)
    }
  } catch (error) {
    ElMessage.error('获取个人信息失败')
  }
}

const handleUpdate = async () => {
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await userStore.editUserInfo(userForm)
        if (response.code === 200) {
          ElMessage.success('个人信息更新成功')
        } else {
          ElMessage.error(response.msg)
        }
      } catch (error) {
        ElMessage.error('更新失败，请稍后重试')
      }
    }
  })
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/login')
}
</script>

<style scoped>
.personal-info-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.personal-info-container h2 {
  margin-bottom: 20px;
  color: #303133;
}

.info-card {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-form-item {
  margin-bottom: 15px;
}
</style>