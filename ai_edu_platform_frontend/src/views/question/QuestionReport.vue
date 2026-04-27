<!-- 答题报告页 - Cyberpunk 2.0 -->
<template>
  <div class="question-report-container">
    <ParticleBackground :zIndex="0" :particleCount="35" particleColor="mixed" :speed="0.3" />
    <h2 class="page-title gradient-text">答题报告</h2>
    <div class="report-card">
      <!-- 分数展示 -->
      <div class="score-section">
        <div class="score-circle" :class="getScoreClass(report.score)">
          <span class="score-number">{{ report.score }}</span>
          <span class="score-text">分</span>
        </div>
        <div class="score-info">
          <h3 class="gradient-text">您的得分</h3>
          <p class="score-level-text">{{ getScoreLevel(report.score) }}</p>
          <p v-if="report.accuracy" class="accuracy">正确率：{{ report.accuracy }}%</p>
        </div>
      </div>

      <!-- 报告内容 -->
      <div class="report-content">
        <h3 class="section-header gradient-text">答题分析</h3>

        <div v-if="report.userOptions" class="analysis-section">
          <h4>您选择的技术栈</h4>
          <div class="tech-tags">
            <el-tag v-for="(option, index) in parseUserOptions(report.userOptions)" :key="index" type="info" size="large" effect="plain">
              {{ option.label }}
            </el-tag>
          </div>
        </div>

        <div v-if="report.trueOptions" class="analysis-section">
          <h4>正确选项</h4>
          <div class="tech-tags">
            <el-tag v-for="(option, index) in parseTrueOptions(report.trueOptions)" :key="index" type="success" size="large" effect="plain">
              {{ option.label }}
            </el-tag>
          </div>
        </div>

        <div v-if="report.comment" class="analysis-section">
          <h4>评价</h4>
          <p>{{ report.comment }}</p>
        </div>

        <div v-if="report.reason" class="analysis-section">
          <h4>评分原因</h4>
          <p>{{ report.reason }}</p>
        </div>

        <div v-if="report.analysis" class="analysis-section">
          <h4>题目解析</h4>
          <p>{{ report.analysis }}</p>
        </div>

        <div v-if="report.suggest" class="analysis-section">
          <h4>建议</h4>
          <p>{{ report.suggest }}</p>
        </div>
      </div>

      <!-- 按钮区域 -->
      <div class="button-section">
        <el-button :type="isCollected ? 'warning' : 'primary'" @click="handleCollect" :loading="collectLoading">
          {{ isCollected ? '取消收藏' : '收藏题目' }}
        </el-button>
        <el-button type="primary" @click="handleRetry" :loading="loading">再来一次</el-button>
        <el-button @click="goBack">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getQuizReport, toggleCollect } from '../../api/questionApi'
import { useUserStore } from '../../store/userStore'
import ParticleBackground from '../../components/common/ParticleBackground.vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const collectLoading = ref(false)
const isCollected = ref(false)

const report = reactive({
  recordId: null, questionId: null, score: 0, accuracy: 0, comment: '', suggest: '', reason: '', analysis: '', createTime: ''
})

onMounted(async () => {
  const recordId = localStorage.getItem('quizRecordId')
  if (!recordId) {
    ElMessage.warning('请先完成答题')
    router.push('/question/input')
    return
  }
  await loadReport(recordId)
})

const loadReport = async (recordId) => {
  loading.value = true
  try {
    const response = await getQuizReport(recordId)
    if (response.code === 200) {
      Object.assign(report, response.data)
      await checkCollectStatus()
    } else {
      ElMessage.error(response.msg || '获取报告失败')
      router.push('/question/input')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '获取报告失败，请稍后重试')
    router.push('/question/input')
  } finally {
    loading.value = false
  }
}

const checkCollectStatus = async () => {
  try {
    const { getCollectList } = await import('../../api/questionApi')
    const response = await getCollectList()
    if (response.code === 200) {
      isCollected.value = (response.data || []).some(item => item.questionId === report.questionId)
    }
  } catch (error) {
    console.error('查询收藏状态失败:', error)
    isCollected.value = false
  }
}

const handleRetry = () => {
  localStorage.removeItem('currentQuestion'); localStorage.removeItem('quizRecordId')
  router.push('/question/input')
}

const goBack = () => { router.push('/home') }

const handleCollect = async () => {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return }
  if (!report.questionId) { ElMessage.error('题目ID不存在'); return }
  collectLoading.value = true
  try {
    const response = await toggleCollect(report.questionId)
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

const getScoreLevel = (score) => {
  if (score >= 90) return '优秀';
  if (score >= 80) return '良好';
  if (score >= 60) return '及格';
  return '不及格'
}

const getScoreClass = (score) => {
  if (score >= 80) return 'excellent';
  if (score >= 60) return 'pass';
  return 'fail'
}

const parseUserOptions = (s) => { try { return JSON.parse(s) } catch (e) { return [] } }
const parseTrueOptions = (s) => { try { return JSON.parse(s) } catch (e) { return [] } }
</script>

<style scoped>
.question-report-container {
  padding: 30px 40px;
  max-width: 1400px;
  margin: 0 auto;
  animation: terminal-fade-in 0.8s ease-out;
  position: relative;
}

.page-title {
  margin-bottom: 24px;
  text-align: center;
  font-size: 30px;
  font-weight: 900;
  letter-spacing: 2px;
}

.report-card {
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border: 1px solid var(--panel-border);
  padding: 36px;
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow-active);
  position: relative;
  z-index: 1;
  animation: breathe 5s ease-in-out infinite;
}

/* 分数展示 */
.score-section {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--divider);
}

.score-circle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  color: white;
  margin-right: 30px;
  transition: transform var(--transition-base);
  border: 3px solid;
}

.score-circle:hover { transform: scale(1.05); }

.score-circle.excellent {
  background: rgba(0, 255, 65, 0.1);
  border-color: var(--neon-green);
  box-shadow: var(--glow-green), 0 0 30px rgba(0, 255, 65, 0.15);
}

.score-circle.pass {
  background: rgba(0, 212, 255, 0.1);
  border-color: var(--neon-cyan);
  box-shadow: var(--glow-cyan), 0 0 30px rgba(0, 212, 255, 0.15);
}

.score-circle.fail {
  background: rgba(255, 0, 128, 0.1);
  border-color: var(--neon-pink);
  box-shadow: var(--glow-pink), 0 0 30px rgba(255, 0, 128, 0.15);
}

.score-number {
  font-size: 48px;
  font-weight: 800;
}

.score-text {
  font-size: 20px;
  margin-left: 4px;
  opacity: 0.8;
}

.score-info { flex: 1; text-align: center; }
.score-info h3 { margin: 0 0 8px; font-size: 20px; font-weight: 700; }
.score-info p { margin: 0; font-size: 16px; color: var(--text-secondary); }
.score-info .accuracy { margin-top: 6px; font-size: 14px; color: var(--neon-purple); }

.score-level-text {
  font-size: 20px;
  font-weight: 700;
}

.score-circle.excellent + .score-info .score-level-text { color: var(--neon-green); text-shadow: var(--glow-text-green); }
.score-circle.pass + .score-info .score-level-text { color: var(--neon-cyan); text-shadow: var(--glow-text-cyan); }
.score-circle.fail + .score-info .score-level-text { color: var(--neon-pink); text-shadow: var(--glow-text-green); }

.section-header {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 1px;
}

.report-content { margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid var(--divider); }

.analysis-section { margin-bottom: 20px; }

.analysis-section h4 {
  margin-bottom: 8px;
  color: var(--neon-purple);
  font-weight: 700;
  font-size: 15px;
}

.analysis-section p {
  line-height: 1.8;
  color: var(--text-primary);
  font-size: 14px;
}

.tech-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 8px; }

.button-section { display: flex; justify-content: center; gap: 16px; }

@media (max-width: 768px) {
  .question-report-container { padding: 20px; }
  .report-card { padding: 24px; }
  .score-circle { width: 120px; height: 120px; }
  .score-number { font-size: 36px; }
}
</style>
