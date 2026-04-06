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
        <div class="salary-level">
          {{ report.salaryLevel }}
        </div>
      </div>
      
      <!-- 报告内容 -->
      <div class="report-content">
        <div class="analysis-section">
          <h4>行业分析</h4>
          <p>{{ report.industryAnalysis }}</p>
        </div>
        
        <div class="analysis-section">
          <h4>技术栈分析</h4>
          <p>{{ report.techAnalysis }}</p>
        </div>
        
        <div class="analysis-section">
          <h4>市场趋势</h4>
          <p>{{ report.marketTrend }}</p>
        </div>
        
        <div class="analysis-section">
          <h4>职业发展建议</h4>
          <ul>
            <li v-for="(suggestion, index) in report.suggestions" :key="index">{{ suggestion }}</li>
          </ul>
        </div>
      </div>
      
      <!-- 按钮区域 -->
      <div class="button-section">
        <el-button type="primary" @click="handleRetry">重新评估</el-button>
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
  salaryRange: '18000-25000元',
  salaryLevel: '中高级水平',
  industryAnalysis: '根据您的技术方向和经验，当前互联网行业对前端开发工程师的需求较大，特别是掌握现代前端框架和TypeScript的开发者。一线城市的薪资水平普遍高于二三线城市，建议您关注一线城市的就业机会。',
  techAnalysis: '您掌握的技术栈符合市场需求，Vue 3、TypeScript、React等技术都是当前前端开发的主流技术，具有较高的市场价值。建议您继续深入学习这些技术，并关注前端性能优化、微前端等新兴领域。',
  marketTrend: '当前前端开发市场呈现稳定增长趋势，随着Web应用的复杂度不断提高，对前端开发者的要求也越来越高。掌握全栈开发能力、具有良好的代码质量和性能优化意识的开发者更受欢迎。',
  suggestions: [
    '建议继续深入学习前端框架的高级特性，如Vue 3的Composition API、React的Hooks等',
    '关注前端性能优化，学习Web Vitals、Core Web Vitals等性能指标',
    '考虑学习一些后端技术，如Node.js，提升全栈开发能力',
    '参与开源项目或个人项目，丰富自己的项目经验',
    '关注行业动态，了解前端领域的最新技术和趋势'
  ]
})

// 页面加载时检查是否有评估数据
onMounted(() => {
  const inputData = localStorage.getItem('salaryInputData')
  if (!inputData) {
    ElMessage.warning('请先填写评估信息')
    router.push('/salary/input')
  }
})

// 重新评估
const handleRetry = () => {
  // 清除本地存储的数据
  localStorage.removeItem('salaryInputData')
  router.push('/salary/input')
}

// 返回首页
const goBack = () => {
  router.push('/home')
}
</script>

<style scoped>
.salary-report-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.salary-report-container h2 {
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