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
  padding: 30px 40px;
  max-width: 1400px;
  margin: 0 auto;
  animation: terminal-fade-in 0.6s ease-out;
  position: relative;
}

.answer-detail-container h2 {
  margin-bottom: 24px;
  text-align: center;
  font-size: 26px;
  font-weight: 800;
  color: var(--neon-green);
  text-shadow: var(--glow-text-green);
  letter-spacing: 2px;
  font-family: 'JetBrains Mono', monospace;
}

.answer-detail-container h2::before {
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
  position: relative;
  z-index: 1;
}

.detail-section {
  margin-bottom: 24px;
}

/* 核心数据概览 */
.score-overview {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 24px;
  background: var(--panel-bg);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-sm);
  margin-bottom: 24px;
}

.score-item {
  text-align: center;
  flex: 1;
}

.score-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
  font-weight: 500;
  font-family: 'JetBrains Mono', monospace;
}

.score-value {
  font-size: 28px;
  font-weight: 800;
  color: var(--neon-cyan);
  line-height: 1.2;
  font-family: 'JetBrains Mono', monospace;
}

.score-value.accuracy {
  color: var(--neon-yellow);
}

.score-value.time {
  font-size: 16px;
  color: var(--text-secondary);
}

.score-divider {
  width: 1px;
  height: 40px;
  background-color: var(--divider-strong);
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

.detail-value.score {
  font-size: 18px;
  font-weight: 700;
  color: var(--neon-cyan);
}

.detail-text {
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-secondary);
  width: 100%;
  margin-top: 8px;
  padding: 16px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: var(--radius-xs);
  border: 1px solid var(--divider);
  border-left: 3px solid var(--neon-cyan);
  font-family: 'JetBrains Mono', monospace;
}

.detail-text.options,
.detail-text.true-options {
  white-space: pre-line;
  line-height: 2;
}

.detail-text.options {
  border-left-color: var(--text-muted);
}

.detail-text.true-options {
  border-left-color: var(--neon-green);
  color: var(--neon-green);
}

.detail-text.user-answer {
  border-left-color: var(--neon-green);
}

.detail-text.comment {
  border-left-color: var(--neon-yellow);
}

.detail-text.reason {
  border-left-color: var(--neon-pink);
}

.detail-text.analysis {
  border-left-color: var(--neon-cyan);
}

.detail-text.suggest {
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