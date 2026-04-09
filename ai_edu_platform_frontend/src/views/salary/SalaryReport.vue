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
  padding: 30px 50px;
  max-width: 1400px;
  margin: 0 auto;
}

.salary-report-container h2 {
  margin-bottom: 30px;
  color: #303133;
  text-align: center;
  font-size: 28px;
  font-weight: 600;
}

.report-card {
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.08);
}

.salary-range-section {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.salary-range-section h3 {
  margin-bottom: 15px;
  color: #303133;
}

.report-info {
  margin-top: 15px;
  text-align: left;
  padding: 0 20px;
}

.report-info p {
  margin: 8px 0;
  font-size: 16px;
  color: #606266;
}

.salary-range {
  margin-bottom: 10px;
}

.salary-number {
  font-size: 48px;
  font-weight: bold;
  color: #409EFF;
}

.salary-level {
  font-size: 18px;
  color: #606266;
}

.report-content {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.analysis-section {
  margin-bottom: 20px;
}

.analysis-section h4 {
  margin-bottom: 10px;
  color: #303133;
}

.analysis-section p {
  line-height: 1.6;
  color: #606266;
}

.analysis-section ul {
  margin: 10px 0 0 20px;
  line-height: 1.6;
  color: #606266;
}

.button-section {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>