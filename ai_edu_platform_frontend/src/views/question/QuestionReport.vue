<!-- 技术挑战答题报告页 -->
<template>
  <div class="question-report-container">
    <h2>技术挑战 - 答题报告</h2>
    <div class="report-card">
      <!-- 分数展示 -->
      <div class="score-section">
        <div class="score-circle">
          <span class="score-number">{{ report.score }}</span>
          <span class="score-text">分</span>
        </div>
        <div class="score-info">
          <h3>您的得分</h3>
          <p>{{ report.score >= 80 ? '优秀' : report.score >= 60 ? '及格' : '不及格' }}</p>
        </div>
      </div>
      
      <!-- 报告内容 -->
      <div class="report-content">
        <h3>答题分析</h3>
        <div class="analysis-section">
          <h4>答案分析</h4>
          <p>{{ report.answerAnalysis }}</p>
        </div>
        
        <div class="analysis-section">
          <h4>技术栈选择分析</h4>
          <p>{{ report.techStackAnalysis }}</p>
        </div>
        
        <div class="analysis-section">
          <h4>建议</h4>
          <ul>
            <li v-for="(suggestion, index) in report.suggestions" :key="index">{{ suggestion }}</li>
          </ul>
        </div>
      </div>
      
      <!-- 按钮区域 -->
      <div class="button-section">
        <el-button type="primary" @click="handleRetry">再来一次</el-button>
        <el-button @click="goBack">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 模拟报告数据
const report = reactive({
  score: 85,
  answerAnalysis: '您的答案结构清晰，能够从多个角度分析前端应用的性能问题，并且提供了具体的优化方案。特别是在页面加载速度和运行时性能方面的分析非常深入，体现了您对前端性能优化的理解。',
  techStackAnalysis: '您选择的技术栈组合合理，Vue 3 + TypeScript + Vite 是现代前端开发的主流技术栈，能够很好地满足高性能前端应用的需求。建议可以考虑添加一些性能监控工具，如 Lighthouse 或 web-vitals，以便更好地监控应用性能。',
  suggestions: [
    '可以进一步了解前端性能优化的最新趋势，如 Web Vitals 和 Core Web Vitals',
    '建议学习一些性能分析工具的使用，如 Chrome DevTools 的 Performance 面板',
    '可以考虑学习一些服务端渲染技术，如 Nuxt.js 或 Next.js，以进一步提升首屏加载速度',
    '建议关注前端安全最佳实践，确保应用的安全性'
  ]
})

// 页面加载时检查是否有答案数据
onMounted(() => {
  const answerData = localStorage.getItem('questionAnswerData')
  if (!answerData) {
    ElMessage.warning('请先完成答题')
    router.push('/question/input')
  }
})

// 再来一次
const handleRetry = () => {
  // 清除本地存储的数据
  localStorage.removeItem('questionInputData')
  localStorage.removeItem('questionAnswerData')
  router.push('/question/input')
}

// 返回首页
const goBack = () => {
  router.push('/home')
}
</script>

<style scoped>
.question-report-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.question-report-container h2 {
  margin-bottom: 20px;
  color: #303133;
  text-align: center;
}

.report-card {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.score-section {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.score-circle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF 0%, #667eea 100%);
  color: white;
  margin-right: 30px;
}

.score-number {
  font-size: 48px;
  font-weight: bold;
}

.score-text {
  font-size: 24px;
  margin-left: 5px;
}

.score-info h3 {
  margin: 0 0 10px 0;
  color: #303133;
}

.score-info p {
  margin: 0;
  font-size: 18px;
  color: #606266;
}

.report-content {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.report-content h3 {
  margin-bottom: 20px;
  color: #303133;
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