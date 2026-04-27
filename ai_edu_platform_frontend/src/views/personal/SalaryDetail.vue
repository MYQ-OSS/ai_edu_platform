<!-- 薪资报告详情页 -->
<template>
  <div class="salary-detail-container">
    <h2>薪资报告详情</h2>
    <div class="detail-card">
      <div class="detail-section">
        <div class="detail-row">
          <div class="detail-item">
            <label class="detail-label">技术方向：</label>
            <span class="detail-value">{{ salaryDetail.direction }}</span>
          </div>
          <div class="detail-item" v-if="salaryDetail.city">
            <label class="detail-label">目标城市：</label>
            <span class="detail-value">{{ salaryDetail.city }}</span>
          </div>
          <div class="detail-item">
            <label class="detail-label">生成时间：</label>
            <span class="detail-value">{{ salaryDetail.createTime }}</span>
          </div>
        </div>
        <div class="detail-item full-width">
          <label class="detail-label">预估薪资：</label>
          <span class="detail-value salary">{{ salaryDetail.salaryRange }}</span>
        </div>
        <div class="detail-item full-width" v-if="salaryDetail.experience">
          <label class="detail-label">工作经验：</label>
          <div class="detail-text">{{ salaryDetail.experience }}</div>
        </div>
        <div class="detail-item full-width" v-if="salaryDetail.aiSuggestion">
          <label class="detail-label">AI建议：</label>
          <div class="detail-text trend">{{ salaryDetail.aiSuggestion }}</div>
        </div>
      </div>
      <div class="button-section">
        <el-button @click="goBack">返回</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSalaryReport } from '../../api/salaryApi'

const router = useRouter()
const route = useRoute()
const reportId = route.params.id

const salaryDetail = reactive({
  id: '',
  userId: '',
  direction: '',
  city: '',
  experience: '',
  salaryRange: '',
  aiSuggestion: '',
  createTime: ''
})

const loading = ref(false)

// 页面加载时获取薪资报告详情
onMounted(async () => {
  await loadSalaryDetail()
})

const loadSalaryDetail = async () => {
  if (!reportId) {
    ElMessage.error('缺少薪资报告ID')
    return
  }
  
  loading.value = true
  try {
    const response = await getSalaryReport(reportId)
    if (response.code === 200) {
      Object.assign(salaryDetail, response.data)
    } else {
      ElMessage.error(response.msg || '获取薪资报告详情失败')
      router.push('/personal/info')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '获取薪资报告详情失败，请稍后重试')
    router.push('/personal/info')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/personal/info')
}
</script>

<style scoped>
.salary-detail-container {
  padding: 30px 40px;
  max-width: 1400px;
  margin: 0 auto;
  animation: terminal-fade-in 0.6s ease-out;
  position: relative;
}

.salary-detail-container h2 {
  margin-bottom: 24px;
  text-align: center;
  font-size: 26px;
  font-weight: 800;
  color: var(--neon-green);
  text-shadow: var(--glow-text-green);
  letter-spacing: 2px;
  font-family: 'JetBrains Mono', monospace;
}

.salary-detail-container h2::before {
  content: '> ';
  color: var(--neon-cyan);
  animation: cursor-blink 1s step-end infinite;
}

.detail-card {
  background: var(--panel-bg);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--panel-border);
  padding: 32px;
  border-radius: var(--radius-md);
  box-shadow: var(--panel-glow);
}

.detail-section {
  margin-bottom: 24px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
  padding: 12px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: var(--radius-xs);
  border: 1px solid var(--divider);
}

.detail-item.full-width {
  flex-direction: column;
  align-items: flex-start;
}

.detail-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.detail-row .detail-item {
  flex: 1;
  min-width: 200px;
  margin-bottom: 0;
}

.detail-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--neon-cyan);
  min-width: 80px;
  margin-right: 12px;
  line-height: 1.8;
  font-family: 'JetBrains Mono', monospace;
}

.detail-value {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.8;
  flex: 1;
  font-family: 'JetBrains Mono', monospace;
}

.detail-value.salary {
  font-size: 24px;
  font-weight: 800;
  color: var(--neon-green);
  text-shadow: var(--glow-text-green);
}

.detail-text {
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-secondary);
  width: 100%;
  margin-top: 8px;
  padding: 12px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: var(--radius-xs);
  border: 1px solid var(--divider);
  border-left: 3px solid var(--neon-cyan);
  font-family: 'JetBrains Mono', monospace;
}

.detail-text.trend {
  border-left-color: var(--neon-green);
}

.button-section {
  display: flex;
  justify-content: center;
  padding-top: 16px;
  border-top: 1px solid var(--divider);
}

.button-section .el-button {
  font-family: 'JetBrains Mono', monospace;
  font-weight: 700;
  letter-spacing: 1px;
}
</style>