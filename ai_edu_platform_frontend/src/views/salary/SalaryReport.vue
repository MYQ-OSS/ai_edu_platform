<!-- 薪资评估报告页 -->
<template>
  <div class="salary-report-container">
    <h2>薪资评估报告</h2>
    <div class="report-card">
      <!-- 薪资范围展示 -->
      <div class="salary-range-section">
        <h3>预估薪资范围</h3>
        <div class="salary-range">
          <span class="salary-number">{{ report.salaryRange }}</span>
        </div>
        <div v-if="report.direction" class="report-info">
          <p><strong>技术方向：</strong>{{ report.direction }}</p>
          <p v-if="report.city"><strong>目标城市：</strong>{{ report.city }}</p>
        </div>
      </div>
      
      <!-- 报告内容 -->
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
      
      <!-- 按钮区域 -->
      <div class="button-section">
        <el-button type="primary" @click="handleRetry" :loading="loading">重新评估</el-button>
        <el-button @click="goBack">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSalaryReport } from '../../api/salaryApi'

const router = useRouter()
const loading = ref(false)

// 报告数据
const report = reactive({
  id: null,
  direction: '',
  city: '',
  experience: '',
  salaryRange: '',
  aiSuggestion: '',
  createTime: ''
})

// 页面加载时获取薪资评估报告
onMounted(async () => {
  const reportId = localStorage.getItem('salaryReportId')
  if (!reportId) {
    ElMessage.warning('请先完成薪资评估')
    router.push('/salary/input')
    return
  }
  
  await loadReport(reportId)
})

// 加载薪资评估报告
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

// 重新评估
const handleRetry = () => {
  // 清除本地存储的数据
  localStorage.removeItem('salaryReportId')
  router.push('/salary/input')
}

// 返回首页
const goBack = () => {
  router.push('/home')
}
</script>

<style scoped>
.salary-report-container {
  padding: 30px 40px;
  max-width: 1400px;
  margin: 0 auto;
  animation: terminal-fade-in 0.6s ease-out;
  position: relative;
}

/* 装饰光斑 */
.salary-report-container::before {
  content: '';
  position: fixed;
  bottom: 10%;
  left: 10%;
  width: 450px;
  height: 450px;
  background: radial-gradient(circle, rgba(0, 255, 65, 0.04) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
  animation: corner-pulse 10s ease-in-out infinite;
  z-index: 0;
}

.salary-report-container h2 {
  margin-bottom: 24px;
  text-align: center;
  font-size: 26px;
  font-weight: 800;
  color: var(--neon-green);
  text-shadow: var(--glow-text-green);
  letter-spacing: 2px;
  font-family: 'JetBrains Mono', monospace;
  position: relative;
  z-index: 1;
}

.salary-report-container h2::before {
  content: '> ';
  color: var(--neon-cyan);
  animation: cursor-blink 1s step-end infinite;
}

.report-card {
  background: var(--panel-bg);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--panel-border);
  padding: 36px;
  border-radius: var(--radius-md);
  box-shadow: var(--panel-glow);
  position: relative;
  z-index: 1;
}

.salary-range-section {
  text-align: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--divider);
}

.salary-range-section h3 {
  margin-bottom: 12px;
  color: var(--neon-cyan);
  font-weight: 700;
  font-family: 'JetBrains Mono', monospace;
}

.report-info {
  margin-top: 12px;
  text-align: left;
  padding: 0 16px;
}

.report-info p {
  margin: 6px 0;
  font-size: 15px;
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
}

.salary-range {
  margin-bottom: 8px;
}

.salary-number {
  font-size: 44px;
  font-weight: 800;
  color: var(--neon-green);
  text-shadow: var(--glow-text-green);
  font-family: 'JetBrains Mono', monospace;
}

.salary-level {
  font-size: 16px;
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
}

.report-content {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--divider);
}

.analysis-section {
  margin-bottom: 20px;
}

.analysis-section h4 {
  margin-bottom: 8px;
  color: var(--text-primary);
  font-weight: 700;
  font-size: 15px;
  font-family: 'JetBrains Mono', monospace;
}

.analysis-section p {
  line-height: 1.8;
  color: var(--text-secondary);
  font-size: 14px;
}

.analysis-section ul {
  margin: 8px 0 0 16px;
  line-height: 1.8;
  color: var(--text-secondary);
}

.button-section {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.button-section .el-button {
  font-family: 'JetBrains Mono', monospace;
  font-weight: 700;
  letter-spacing: 1px;
}
</style>