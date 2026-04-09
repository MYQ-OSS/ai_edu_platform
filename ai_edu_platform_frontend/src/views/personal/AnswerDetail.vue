<!-- 答题历史详情页 -->
<template>
  <div class="answer-detail-container">
    <h2>答题详情</h2>
    <div class="detail-card">
      <div class="detail-section">
        <!-- 核心数据展示 -->
        <div class="score-overview">
          <div class="score-item">
            <div class="score-label">得分</div>
            <div class="score-value">{{ answerDetail.score }}</div>
          </div>
          <div class="score-divider"></div>
          <div class="score-item">
            <div class="score-label">正确率</div>
            <div class="score-value accuracy">{{ answerDetail.accuracy }}%</div>
          </div>
          <div class="score-divider"></div>
          <div class="score-item">
            <div class="score-label">答题时间</div>
            <div class="score-value time">{{ answerDetail.createTime }}</div>
          </div>
        </div>

        <!-- 用户答案 -->
        <div class="detail-item full-width" v-if="answerDetail.userOptions">
          <label class="detail-label">您的选项：</label>
          <div class="detail-text options">{{ formatOptions(answerDetail.userOptions) }}</div>
        </div>
        <div class="detail-item full-width" v-if="answerDetail.userAnswer">
          <label class="detail-label">您的文本答案：</label>
          <div class="detail-text user-answer">{{ answerDetail.userAnswer }}</div>
        </div>

        <!-- 正确选项 -->
        <div class="detail-item full-width" v-if="answerDetail.trueOptions">
          <label class="detail-label">正确选项：</label>
          <div class="detail-text true-options">{{ formatOptions(answerDetail.trueOptions) }}</div>
        </div>

        <!-- AI评价 -->
        <div class="detail-item full-width" v-if="answerDetail.comment">
          <label class="detail-label">AI评价：</label>
          <div class="detail-text comment">{{ answerDetail.comment }}</div>
        </div>

        <!-- 评分原因 -->
        <div class="detail-item full-width" v-if="answerDetail.reason">
          <label class="detail-label">评分原因：</label>
          <div class="detail-text reason">{{ answerDetail.reason }}</div>
        </div>

        <!-- 题目解析 -->
        <div class="detail-item full-width" v-if="answerDetail.analysis">
          <label class="detail-label">题目解析：</label>
          <div class="detail-text analysis">{{ answerDetail.analysis }}</div>
        </div>

        <!-- 学习建议 -->
        <div class="detail-item full-width" v-if="answerDetail.suggest">
          <label class="detail-label">学习建议：</label>
          <div class="detail-text suggest">{{ answerDetail.suggest }}</div>
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
import { getQuizReport } from '../../api/questionApi'

const router = useRouter()
const route = useRoute()
const recordId = route.params.id

const answerDetail = reactive({
  recordId: '',
  questionId: '',
  userOptions: '',
  userAnswer: '',
  score: 0,
  comment: '',
  suggest: '',
  reason: '',
  trueOptions: '',
  analysis: '',
  accuracy: 0,
  createTime: ''
})

const loading = ref(false)

// 页面加载时获取答题详情
onMounted(async () => {
  await loadAnswerDetail()
})

const loadAnswerDetail = async () => {
  if (!recordId) {
    ElMessage.error('缺少答题记录ID')
    return
  }
  
  loading.value = true
  try {
    const response = await getQuizReport(recordId)
    if (response.code === 200) {
      Object.assign(answerDetail, response.data)
    } else {
      ElMessage.error(response.msg || '获取答题详情失败')
      router.push('/personal/info')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '获取答题详情失败，请稍后重试')
    router.push('/personal/info')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/personal/info')
}

// 格式化选项JSON为可读文本
const formatOptions = (optionsJson) => {
  if (!optionsJson) return ''
  try {
    const options = JSON.parse(optionsJson)
    if (Array.isArray(options)) {
      // 如果数组元素是对象，提取label或value字段，每个选项单独一行
      return options.map((opt, index) => {
        let text = ''
        if (typeof opt === 'object') {
          text = opt.label || opt.value || JSON.stringify(opt)
        } else {
          text = String(opt)
        }
        return `${index + 1}. ${text}`
      }).join('\n')
    }
    return optionsJson
  } catch (e) {
    return optionsJson
  }
}
</script>

<style scoped>
.answer-detail-container {
  padding: 30px 50px;
  max-width: 1400px;
  margin: 0 auto;
}

.answer-detail-container h2 {
  margin-bottom: 30px;
  color: #303133;
  text-align: center;
  font-size: 28px;
  font-weight: 600;
}

.detail-card {
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.08);
}

.detail-section {
  margin-bottom: 30px;
}

/* 核心数据概览 */
.score-overview {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

.score-item {
  text-align: center;
  flex: 1;
}

.score-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 10px;
  font-weight: 500;
}

.score-value {
  font-size: 32px;
  font-weight: 700;
  color: white;
  line-height: 1.2;
}

.score-value.accuracy {
  color: #ffd700;
}

.score-value.time {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.95);
}

.score-divider {
  width: 1px;
  height: 50px;
  background-color: rgba(255, 255, 255, 0.3);
}

.detail-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fafafa;
  border-radius: 8px;
}

.detail-item.full-width {
  flex-direction: column;
  align-items: flex-start;
}

.detail-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.detail-row .detail-item {
  flex: 1;
  min-width: 200px;
  margin-bottom: 0;
}

.detail-label {
  font-size: 16px;
  font-weight: 600;
  color: #606266;
  min-width: 100px;
  margin-right: 15px;
  line-height: 1.8;
}

.detail-value {
  font-size: 16px;
  color: #303133;
  line-height: 1.8;
  flex: 1;
}

.detail-value.score {
  font-size: 20px;
  font-weight: 600;
  color: #409EFF;
}

.detail-text {
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
  width: 100%;
  margin-top: 10px;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  border-left: 4px solid #409EFF;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.detail-text.options,
.detail-text.true-options {
  white-space: pre-line;
  line-height: 2;
}

.detail-text.options {
  border-left-color: #909399;
  background: linear-gradient(to right, #f4f4f5 0%, #ffffff 100%);
  font-weight: 600;
}

.detail-text.true-options {
  border-left-color: #67c23a;
  background: linear-gradient(to right, #f0f9ff 0%, #ffffff 100%);
  font-weight: 600;
  color: #67c23a;
}

.detail-text.user-answer {
  border-left-color: #67c23a;
  background: linear-gradient(to right, #f0f9ff 0%, #ffffff 100%);
}

.detail-text.comment {
  border-left-color: #e6a23c;
  background: linear-gradient(to right, #fdf6ec 0%, #ffffff 100%);
}

.detail-text.reason {
  border-left-color: #f56c6c;
  background: linear-gradient(to right, #fef0f0 0%, #ffffff 100%);
}

.detail-text.analysis {
  border-left-color: #409EFF;
  background: linear-gradient(to right, #ecf5ff 0%, #ffffff 100%);
}

.detail-text.suggest {
  border-left-color: #67c23a;
  background: linear-gradient(to right, #f0f9ff 0%, #ffffff 100%);
}

.button-section {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}
</style>