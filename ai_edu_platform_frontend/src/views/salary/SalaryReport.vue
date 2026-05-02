<!-- 薪资报告页 - Cyberpunk 2.0 -->
<template>
  <div class="salary-report-container">
    <ParticleBackground :zIndex="0" :particleCount="30" particleColor="cyan" :speed="0.3" />
    <h2 class="page-title gradient-text">薪资评估报告</h2>
    <div class="report-card">
      <div class="salary-range-section">
        <h3 class="section-subtitle">预估薪资范围</h3>
        <div class="salary-range">
          <span class="salary-number">{{ report.salaryRange }}</span>
        </div>
        <div v-if="report.direction" class="report-info">
          <p><strong>技术方向：</strong>{{ report.direction }}</p>
          <p v-if="report.city"><strong>目标城市：</strong>{{ report.city }}</p>
        </div>
      </div>

      <div class="report-content">
        <div v-if="report.aiSuggestion" class="analysis-section">
          <h4>AI评估建议</h4>
          <p>{{ report.aiSuggestion }}</p>
        </div>
        <div v-if="report.experience" class="analysis-section">
          <h4>您的经历</h4>
          <p>{{ report.experience }}</p>
        </div>
        <div v-if="report.createTime" class="analysis-section">
          <h4>评估时间</h4>
          <p>{{ report.createTime }}</p>
        </div>
      </div>

      <div class="button-section">
        <el-button type="primary" @click="handleRetry" :loading="loading">重新评估</el-button>
        <el-button @click="goBack" class="btn-back">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSalaryReport } from '../../api/salaryApi'
import ParticleBackground from '../../components/common/ParticleBackground.vue'

defineOptions({ name: 'SalaryReport' })

const router = useRouter()
const loading = ref(false)

const report = reactive({
  id: null, direction: '', city: '', experience: '', salaryRange: '', aiSuggestion: '', createTime: ''
})

onMounted(async () => {
  const reportId = localStorage.getItem('salaryReportId')
  if (!reportId) {
    ElMessage.warning('请先完成薪资评估')
    router.push('/salary/input')
    return
  }
  await loadReport(reportId)
})

const loadReport = async (reportId) => {
  loading.value = true
  try {
    const response = await getSalaryReport(reportId)
    if (response.code === 200) {
      Object.assign(report, response.data)
    } else {
      ElMessage.error(response.msg || '获取报告失败')
      router.push('/salary/input')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '获取报告失败，请稍后重试')
    router.push('/salary/input')
  } finally {
    loading.value = false
  }
}

const handleRetry = () => {
  localStorage.removeItem('salaryReportId')
  router.push('/salary/input')
}

const goBack = () => { router.push('/home') }
</script>

<style scoped>
.salary-report-container {
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

.salary-range-section {
  text-align: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--divider);
}

.section-subtitle {
  margin-bottom: 12px;
  color: var(--neon-cyan);
  font-weight: 700;
  letter-spacing: 1px;
}

.report-info { margin-top: 12px; text-align: left; padding: 0 16px; }
.report-info p { margin: 6px 0; font-size: 15px; color: var(--text-secondary); }
.report-info strong { color: var(--neon-purple); }

.salary-range { margin-bottom: 8px; }

.salary-number {
  font-size: 48px;
  font-weight: 800;
  color: var(--neon-cyan);
  text-shadow: var(--glow-text-cyan), 0 0 30px rgba(0, 212, 255, 0.3);
  letter-spacing: 2px;
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

.button-section { display: flex; justify-content: center; gap: 16px; }

.btn-back {
  background: transparent !important;
  border: 1px solid var(--neon-cyan) !important;
  color: var(--neon-cyan) !important;
}

.btn-back:hover {
  background: rgba(0, 212, 255, 0.1) !important;
  box-shadow: var(--glow-cyan) !important;
}

@media (max-width: 768px) {
  .salary-report-container { padding: 20px; }
  .report-card { padding: 24px; }
  .salary-number { font-size: 32px; }
}
</style>
