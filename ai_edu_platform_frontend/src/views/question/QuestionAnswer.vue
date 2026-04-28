<!-- 技术挑战答题页 - Cyberpunk 2.0 -->
<template>
  <div class="question-answer-container">
    <ParticleBackground :zIndex="0" :particleCount="15" particleColor="mixed" :speed="0.2" :interactive="false" />
    <h2 class="page-title gradient-text">技术挑战</h2>

    <!-- 计时器显示 -->
    <div v-if="hasTimeLimit" class="timer-section" :class="{'timer-warning': remainingTime <= 60}">
      <el-icon class="timer-icon">&#9202;</el-icon>
      <span class="timer-text">剩余时间：</span>
      <span class="timer-value">{{ formatTime(remainingTime) }}</span>
    </div>

    <div class="answer-card">
      <!-- 题目区域 -->
      <div class="question-section">
        <h3 class="section-title gradient-text">题目</h3>
        <div class="question-content">
          {{ question.title }}
          <br /><br />
          {{ question.content }}
        </div>
      </div>

      <!-- 技术栈选择区域 -->
      <div class="tech-stack-section">
        <h3 class="section-title">请选择技术栈</h3>
        <div class="tech-stack-cards">
          <div
            v-for="tech in techStacks"
            :key="tech.id"
            class="tech-card"
            :class="{ active: selectedTechStacks.includes(tech.id) }"
            @click="toggleTechStack(tech.id)"
          >
            <div class="tech-name">{{ tech.name }}</div>
          </div>
        </div>
      </div>

      <!-- 答题区域 -->
      <div class="answer-section">
        <h3 class="section-title">您的答案</h3>
        <el-input
          v-model="answer"
          type="textarea"
          placeholder="请输入您的答案（技术实现或架构设计思路）"
          :rows="8"
        />
      </div>

      <!-- 选中的技术栈 -->
      <div v-if="selectedTechStacks.length > 0" class="selected-tech-section">
        <h3 class="section-title">已选择的技术栈</h3>
        <div class="selected-tech-tags">
          <el-tag
            v-for="techId in selectedTechStacks"
            :key="techId"
            closable
            @close="removeTechStack(techId)"
            effect="plain"
          >
            {{ getTechNameById(techId) }}
          </el-tag>
        </div>
      </div>

      <!-- 按钮区域 -->
      <div class="button-section">
        <el-button :type="isCollected ? 'warning' : 'primary'" @click="handleCollect" :loading="collectLoading">
          {{ isCollected ? '已收藏' : '收藏题目' }}
        </el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">提交答案</el-button>
        <el-button @click="goBack">返回</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitQuiz, toggleCollect, getCollectList } from '../../api/questionApi'
import { useUserStore } from '../../store/userStore'
import ParticleBackground from '../../components/common/ParticleBackground.vue'

defineOptions({ name: 'QuestionAnswer' })

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const collectLoading = ref(false)
const isCollected = ref(false)
const answer = ref('')
const selectedTechStacks = ref([])

const fromPage = ref(route.query.from || 'input')

const hasTimeLimit = ref(false)
const timeLimit = ref(0)
const remainingTime = ref(0)
let timerInterval = null
let isAutoSubmitting = ref(false)

const question = reactive({
  id: null, title: '', content: '', options: [], timeLimit: 0
})

const techStacks = ref([])

onMounted(() => {
  const currentQuestion = localStorage.getItem('currentQuestion')
  if (!currentQuestion) {
    ElMessage.warning('请先生成题目')
    router.push('/question/input')
    return
  }
  try {
    const questionData = JSON.parse(currentQuestion)
    question.id = questionData.questionId
    question.title = questionData.questionName
    question.content = questionData.questionDesc
    question.timeLimit = questionData.timeLimit || 0
    if (questionData.options) {
      try {
        const options = JSON.parse(questionData.options)
        question.options = options
        techStacks.value = options.map((opt, index) => ({
          id: index + 1, name: opt.label, value: opt.value
        }))
      } catch (e) {
        console.error('解析题目选项失败:', e)
      }
    }
    initTimer()
    checkCollectStatus()
  } catch (error) {
    ElMessage.error('题目数据格式错误')
    router.push('/question/input')
  }
})

const initTimer = () => {
  if (question.timeLimit && question.timeLimit > 0) {
    hasTimeLimit.value = true
    timeLimit.value = question.timeLimit * 60
    remainingTime.value = question.timeLimit * 60
    startTimer()
  }
}

const startTimer = () => {
  if (timerInterval) clearInterval(timerInterval)
  timerInterval = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      handleTimeUp()
    }
  }, 1000)
}

const handleTimeUp = () => {
  stopTimer()
  if (isAutoSubmitting.value) return
  isAutoSubmitting.value = true
  ElMessage.warning('答题时间已到，系统将自动提交答案')
  setTimeout(() => autoSubmitAnswer(), 500)
}

const autoSubmitAnswer = async () => {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return }
  if (!question.id) { ElMessage.error('题目ID不存在'); return }
  loading.value = true
  try {
    const userOptions = selectedTechStacks.value.length > 0
      ? selectedTechStacks.value.map(techId => {
          const tech = techStacks.value.find(t => t.id === techId)
          return { label: tech.name, value: tech.value }
        })
      : []
    const userAnswer = answer.value && answer.value.trim() ? answer.value : '空'
    const requestData = { userId: userStore.userInfo?.id, questionId: question.id, userOptions: JSON.stringify(userOptions), userAnswer }
    const response = await submitQuiz(requestData)
    if (response.code === 200) {
      ElMessage.success('答题结果已自动提交')
      localStorage.setItem('quizRecordId', response.data.recordId)
      router.push('/question/report')
    } else {
      ElMessage.error(response.msg || '提交失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '提交失败，请稍后重试')
  } finally {
    loading.value = false; isAutoSubmitting.value = false
  }
}

const stopTimer = () => { if (timerInterval) { clearInterval(timerInterval); timerInterval = null } }

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

onUnmounted(() => { stopTimer() })

const toggleTechStack = (techId) => {
  const index = selectedTechStacks.value.indexOf(techId)
  if (index === -1) selectedTechStacks.value.push(techId)
  else selectedTechStacks.value.splice(index, 1)
}

const removeTechStack = (techId) => {
  const index = selectedTechStacks.value.indexOf(techId)
  if (index !== -1) selectedTechStacks.value.splice(index, 1)
}

const getTechNameById = (techId) => {
  const tech = techStacks.value.find(t => t.id === techId)
  return tech ? tech.name : ''
}

const handleSubmit = async () => {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return }
  if (!answer.value || !answer.value.trim()) { ElMessage.warning('请输入答案'); return }
  if (selectedTechStacks.value.length === 0) { ElMessage.warning('请至少选择一个技术栈'); return }
  if (!question.id) { ElMessage.error('题目ID不存在'); return }
  stopTimer()
  loading.value = true
  try {
    const userOptions = selectedTechStacks.value.map(techId => {
      const tech = techStacks.value.find(t => t.id === techId)
      return { label: tech.name, value: tech.value }
    })
    const requestData = { userId: userStore.userInfo?.id, questionId: question.id, userOptions: JSON.stringify(userOptions), userAnswer: answer.value }
    const response = await submitQuiz(requestData)
    if (response.code === 200) {
      ElMessage.success('答题结果提交成功')
      localStorage.setItem('quizRecordId', response.data.recordId)
      router.push('/question/report')
    } else {
      ElMessage.error(response.msg || '提交失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '提交失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const checkCollectStatus = async () => {
  if (!userStore.isLoggedIn || !question.id) return
  try {
    const response = await getCollectList()
    if (response.code === 200) {
      isCollected.value = (response.data || []).some(item => item.questionId === question.id)
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

const goBack = () => {
  if (fromPage.value === 'personal') router.push('/personal/info')
  else router.push('/question/input')
}

const handleCollect = async () => {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return }
  if (!question.id) { ElMessage.error('题目ID不存在'); return }
  collectLoading.value = true
  try {
    const response = await toggleCollect(question.id)
    if (response.code === 200) {
      isCollected.value = !isCollected.value
      ElMessage.success(isCollected.value ? '收藏成功' : '取消收藏成功')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '操作失败，请稍后重试')
  } finally {
    collectLoading.value = false
  }
}
</script>

<style scoped>
.question-answer-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  animation: terminal-fade-in 0.8s ease-out;
  position: relative;
}

.page-title {
  margin-bottom: 16px;
  text-align: center;
  font-size: 28px;
  font-weight: 900;
  letter-spacing: 2px;
}

.timer-section {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 24px;
  margin-bottom: 16px;
  background: var(--panel-bg-strong);
  backdrop-filter: blur(12px);
  border-radius: var(--radius-sm);
  border: 1px solid var(--neon-cyan);
  box-shadow: var(--glow-cyan);
  position: relative;
  z-index: 1;
  will-change: auto;
}

.timer-section.timer-warning {
  background: rgba(255, 0, 128, 0.1);
  border-color: var(--neon-pink);
  animation: neon-pulse 1s infinite;
  box-shadow: var(--glow-pink);
}

.timer-icon { font-size: 20px; color: var(--neon-cyan); margin-right: 8px; }
.timer-section.timer-warning .timer-icon { color: var(--neon-pink); }

.timer-text { font-size: 14px; color: var(--text-secondary); margin-right: 8px; }
.timer-value { font-size: 22px; font-weight: 800; color: var(--neon-cyan); text-shadow: var(--glow-text-cyan); letter-spacing: 2px; }
.timer-section.timer-warning .timer-value { color: var(--neon-pink); text-shadow: 0 0 8px rgba(255, 0, 128, 0.6); }

.answer-card {
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border: 1px solid var(--panel-border);
  padding: 32px;
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow-active);
  position: relative;
  z-index: 1;
  animation: breathe 5s ease-in-out infinite;
  transform: translateZ(0);
  -webkit-transform: translateZ(0);
}

.section-title {
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 700;
  color: var(--neon-cyan);
  letter-spacing: 0.5px;
}

.question-section, .tech-stack-section, .answer-section, .selected-tech-section {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--divider);
}

.question-content {
  line-height: 1.8;
  color: var(--text-primary);
  font-size: 15px;
}

.tech-stack-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
}

.tech-card {
  display: flex;
  align-items: center;
  justify-content: center;
  aspect-ratio: 1.3 / 1;
  padding: 16px;
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all var(--transition-base);
  background: rgba(0, 212, 255, 0.03);
  word-break: break-word;
  font-family: inherit;
  will-change: transform, box-shadow, border-color, background;
}

.tech-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--panel-glow-active);
  border-color: var(--neon-purple);
  background: rgba(180, 74, 255, 0.05);
}

.tech-card.active {
  border-color: var(--neon-cyan);
  background: rgba(0, 212, 255, 0.08);
  box-shadow: var(--glow-cyan);
}

.tech-card.active .tech-name {
  color: var(--neon-cyan);
  text-shadow: var(--glow-text-cyan);
  font-weight: 700;
}

.tech-name {
  font-size: 14px;
  color: var(--text-primary);
  text-align: center;
  line-height: 1.5;
  font-weight: 500;
}

.selected-tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.el-tag {
  background: rgba(0, 212, 255, 0.1) !important;
  border-color: rgba(0, 212, 255, 0.3) !important;
  color: var(--neon-cyan) !important;
}

.button-section {
  display: flex;
  justify-content: center;
  gap: 16px;
}

@media (max-width: 768px) {
  .question-answer-container { padding: 15px; }
  .answer-card { padding: 24px; }
  .tech-stack-cards { grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); }
}
</style>
