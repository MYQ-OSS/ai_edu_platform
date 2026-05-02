<!-- 技术挑战输入页 - Cyberpunk 2.0 -->
<template>
  <div class="question-input-container">
    <ParticleBackground :zIndex="0" :particleCount="35" particleColor="mixed" :speed="0.3" />
    <h2 class="page-title gradient-text">技术挑战</h2>
    <div class="input-card">
      <el-form :model="inputForm" :rules="rules" ref="inputFormRef" label-width="140px">
        <el-form-item label="身份定位" prop="identity" required>
          <el-input v-model="inputForm.identity" placeholder="请输入您的身份（如：学生）" size="large" />
        </el-form-item>

        <el-form-item label="技术方向" prop="techDirection" required>
          <el-select v-model="inputForm.techDirection" placeholder="请选择技术方向" size="large">
            <el-option
              v-for="item in techDirections"
              :key="item.dictCode"
              :label="item.dictName"
              :value="item.dictName"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="期望薪资" prop="expectedSalary" required>
          <el-input v-model.number="inputForm.expectedSalary" type="number" placeholder="请输入期望薪资（元/月）" size="large" />
        </el-form-item>

        <el-form-item label="答题时间限制">
          <el-input v-model.number="inputForm.timeLimit" type="number" placeholder="请输入限定时间（分钟，默认30分钟）" size="large" />
        </el-form-item>

        <el-form-item label="目标就业城市">
          <el-input v-model="inputForm.city" placeholder="请输入您期望就业的城市" size="large" />
        </el-form-item>
      </el-form>

      <div class="button-group">
        <el-button @click="handleSubmit" :loading="loading" size="large" class="btn-start">开始挑战</el-button>
        <el-button @click="goBack" size="large" class="btn-back">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { generateQuiz, getTechDirections } from '../../api/questionApi'
import { useUserStore } from '../../store/userStore'
import ParticleBackground from '../../components/common/ParticleBackground.vue'

const router = useRouter()
const userStore = useUserStore()
const inputFormRef = ref(null)
const loading = ref(false)
const techDirections = ref([])

const inputForm = reactive({
  identity: '', techDirection: '', expectedSalary: '', timeLimit: 30, city: ''
})

const rules = {
  identity: [
    { required: true, message: '请输入身份', trigger: 'blur' },
    { max: 50, message: '身份长度不超过50位', trigger: 'blur' }
  ],
  techDirection: [
    { required: true, message: '请选择技术方向', trigger: 'change' }
  ],
  expectedSalary: [
    { required: true, message: '请输入期望薪资', trigger: 'blur' },
    { type: 'number', message: '请输入有效的薪资', trigger: 'blur' }
  ]
}

onMounted(async () => {
  await loadTechDirections()
})

const loadTechDirections = async () => {
  try {
    const response = await getTechDirections()
    if (response.code === 200) {
      techDirections.value = response.data || []
    }
  } catch (error) {
    console.error('获取技术方向失败:', error)
  }
}

const handleSubmit = async () => {
  await inputFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!userStore.isLoggedIn) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }
      loading.value = true
      try {
        const requestData = {
          direction: inputForm.techDirection,
          targetSalary: parseInt(inputForm.expectedSalary),
          identity: inputForm.identity || userStore.userInfo?.identity || '',
          city: inputForm.city || userStore.userInfo?.city || '',
          timeLimit: inputForm.timeLimit || 30
        }
        const response = await generateQuiz(requestData)
        if (response.code === 200) {
          const msg = response.msg || ''
          if (msg.includes('已自动从题库中找到')) {
            ElMessage.warning(msg)
          } else {
            ElMessage.success(msg || '题目生成成功')
          }
          const questionData = { ...response.data, timeLimit: inputForm.timeLimit || 0 }
          localStorage.setItem('currentQuestion', JSON.stringify(questionData))
          router.push('/question/answer')
        } else {
          ElMessage.error(response.msg || '题目生成失败')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.msg || '题目生成失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const goBack = () => {
  router.push('/home')
}
</script>

<style scoped>
.question-input-container {
  padding: 40px;
  max-width: 900px;
  margin: 0 auto;
  animation: terminal-fade-in 0.8s ease-out;
  position: relative;
}

.page-title {
  margin-bottom: 30px;
  text-align: center;
  font-size: 30px;
  font-weight: 900;
  letter-spacing: 2px;
  position: relative;
  z-index: 1;
}

.input-card {
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border: 1px solid var(--panel-border);
  padding: 40px;
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow-active);
  position: relative;
  z-index: 1;
  animation: breathe 5s ease-in-out infinite;
}

.el-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.el-form-item {
  width: 100%;
  max-width: 600px;
  margin-bottom: 24px;
}

.el-form-item :deep(.el-form-item__content) {
  justify-content: center !important;
}

.el-form-item :deep(.el-form-item__label) {
  color: var(--text-secondary);
  font-size: 13px;
}

/* 下拉选择器样式 */
.el-form-item :deep(.el-select) {
  width: 100%;
}

.el-form-item :deep(.el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6) !important;
  border: 1px solid var(--panel-border) !important;
}

/* 下拉菜单样式 */
:deep(.el-select-dropdown) {
  background: var(--panel-bg-strong) !important;
  border: 1px solid var(--panel-border) !important;
}

:deep(.el-select-dropdown__item) {
  background: transparent !important;
  color: var(--text-primary) !important;
}

:deep(.el-select-dropdown__item.is-hovering) {
  background: rgba(0, 212, 255, 0.08) !important;
  color: var(--neon-cyan) !important;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 30px;
}

.button-group .el-button {
  min-width: 140px;
  font-weight: 700;
  letter-spacing: 1px;
  background: transparent !important;
  border: 1px solid var(--neon-cyan) !important;
  color: var(--neon-cyan) !important;
}

.button-group .el-button:hover {
  background: rgba(0, 212, 255, 0.1) !important;
  box-shadow: var(--glow-cyan) !important;
}

@media (max-width: 768px) {
  .question-input-container { padding: 20px; }
  .input-card { padding: 24px; }
}
</style>
