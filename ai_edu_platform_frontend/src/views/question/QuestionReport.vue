<!-- 技术挑战答题报告页 -->
<template>
  <div class="question-report-container">
    <h2>技术挑战 - 答题报告</h2>
    <div class="report-card">
      <!-- 分数展示 -->
      <div class="score-section">
        <div class="score-circle" :class="getScoreClass(report.score)">
          <span class="score-number">{{ report.score }}</span>
          <span class="score-text">分</span>
        </div>
        <div class="score-info">
          <h3>您的得分</h3>
          <p>{{ getScoreLevel(report.score) }}</p>
          <p v-if="report.accuracy" class="accuracy">正确率：{{ report.accuracy }}%</p>
        </div>
      </div>
      
      <!-- 报告内容 -->
      <div class="report-content">
        <h3>答题分析</h3>
        
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
        <el-button 
          :type="isCollected ? 'warning' : 'primary'" 
          @click="handleCollect"
          :loading="collectLoading"
        >
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
import { getQuizReport, collectQuiz } from '../../api/questionApi'
import { useUserStore } from '../../store/userStore'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const collectLoading = ref(false)
const isCollected = ref(false)

// 报告数据
const report = reactive({
  recordId: null,
  questionId: null,
  score: 0,
  accuracy: 0,
  comment: '',
  suggest: '',
  reason: '',
  analysis: '',
  createTime: ''
})

// 页面加载时获取答题报告
onMounted(async () => {
  const recordId = localStorage.getItem('quizRecordId')
  if (!recordId) {
    ElMessage.warning('请先完成答题')
    router.push('/question/input')
    return
  }
  
  await loadReport(recordId)
})

// 加载答题报告
const loadReport = async (recordId) => {
  loading.value = true
  try {
    const response = await getQuizReport(recordId)
    if (response.code === 200) {
      Object.assign(report, response.data)
      // TODO: 查询收藏状态（如果后端有接口）
      // 暂时默认为未收藏
      isCollected.value = false
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

// 再来一次
const handleRetry = () => {
  // 清除本地存储的数据
  localStorage.removeItem('currentQuestion')
  localStorage.removeItem('quizRecordId')
  router.push('/question/input')
}

// 返回首页
const goBack = () => {
  router.push('/home')
}

// 收藏/取消收藏
const handleCollect = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (!report.questionId) {
    ElMessage.error('题目ID不存在')
    return
  }
  
  collectLoading.value = true
  try {
    const requestData = {
      userId: userStore.userInfo?.id,
      questionId: report.questionId,
      isCollect: !isCollected.value
    }
    
    const response = await collectQuiz(requestData)
    
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
// 根据分数获取等级
const getScoreLevel = (score) => {
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 60) return '及格'
  return '不及格'
}

// 根据分数获取样式类
const getScoreClass = (score) => {
  if (score >= 80) return 'excellent'
  if (score >= 60) return 'pass'
  return 'fail'
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
  color: white;
  margin-right: 30px;
  transition: all 0.3s ease;
}

.score-circle.excellent {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.score-circle.pass {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
}

.score-circle.fail {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
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

.score-info .accuracy {
  margin-top: 8px;
  font-size: 16px;
  color: #909399;
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